package com.neurowallet.service;

import com.neurowallet.dto.TransactionRequest;
import com.neurowallet.model.Transaction;
import com.neurowallet.model.Wallet;
import com.neurowallet.repository.TransactionRepository;
import com.neurowallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    
    // Exchange rates (simplified for demo)
    private static final Map<String, BigDecimal> EXCHANGE_RATES = new HashMap<>();
    static {
        // Base: USD
        EXCHANGE_RATES.put("USD_MYR", new BigDecimal("4.50"));
        EXCHANGE_RATES.put("USD_SGD", new BigDecimal("1.35"));
        EXCHANGE_RATES.put("MYR_USD", new BigDecimal("0.22"));
        EXCHANGE_RATES.put("MYR_SGD", new BigDecimal("0.30"));
        EXCHANGE_RATES.put("SGD_USD", new BigDecimal("0.74"));
        EXCHANGE_RATES.put("SGD_MYR", new BigDecimal("3.33"));
    }
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private RiskService riskService;
    
    @Transactional
    public Transaction createTransaction(TransactionRequest request, String email) {
        logger.info("Creating {} transaction for wallet ID: {}", request.getType(), request.getWalletId());
        
        Wallet wallet = walletRepository.findById(request.getWalletId())
            .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        // Verify ownership
        if (!wallet.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access to wallet");
        }
        
        // Check wallet status
        if (wallet.getStatus() == Wallet.WalletStatus.FROZEN) {
            throw new RuntimeException("Wallet is frozen. Cannot perform transaction.");
        }
        
        BigDecimal amount = request.getAmount();
        Transaction.TransactionType type = Transaction.TransactionType.valueOf(request.getType().toUpperCase());
        
        // Handle different transaction types
        switch (type) {
            case DEPOSIT:
                // Add money to wallet (from external source)
                wallet.setBalance(wallet.getBalance().add(amount));
                break;
                
            case WITHDRAWAL:
                // Remove money from wallet (to external source)
                if (wallet.getBalance().compareTo(amount) < 0) {
                    throw new RuntimeException("Insufficient balance");
                }
                wallet.setBalance(wallet.getBalance().subtract(amount));
                amount = amount.negate(); // Store as negative
                break;
                
            case TRANSFER:
                // Transfer between wallets
                Long toWalletId = request.getToWalletId();
                if (toWalletId == null) {
                    throw new RuntimeException("Destination wallet required for transfer");
                }
                
                Wallet toWallet = walletRepository.findById(toWalletId)
                    .orElseThrow(() -> new RuntimeException("Destination wallet not found"));
                
                // Verify ownership of destination wallet
                if (!toWallet.getUser().getEmail().equals(email)) {
                    throw new RuntimeException("Unauthorized access to destination wallet");
                }
                
                if (toWallet.getStatus() == Wallet.WalletStatus.FROZEN) {
                    throw new RuntimeException("Destination wallet is frozen");
                }
                
                // Check sufficient balance
                if (wallet.getBalance().compareTo(amount) < 0) {
                    throw new RuntimeException("Insufficient balance");
                }
                
                // Currency conversion if needed
                BigDecimal convertedAmount = amount;
                if (!wallet.getCurrency().equals(toWallet.getCurrency())) {
                    String rateKey = wallet.getCurrency() + "_" + toWallet.getCurrency();
                    BigDecimal rate = EXCHANGE_RATES.get(rateKey);
                    if (rate == null) {
                        throw new RuntimeException("Exchange rate not available");
                    }
                    convertedAmount = amount.multiply(rate);
                    logger.info("Converting {} {} to {} {}", amount, wallet.getCurrency(), 
                               convertedAmount, toWallet.getCurrency());
                }
                
                // Deduct from source wallet
                wallet.setBalance(wallet.getBalance().subtract(amount));
                
                // Add to destination wallet
                toWallet.setBalance(toWallet.getBalance().add(convertedAmount));
                walletRepository.save(toWallet);
                
                // Store transfer amount as negative for source wallet
                amount = amount.negate();
                break;
                
            default:
                throw new RuntimeException("Invalid transaction type");
        }
        
        // Save wallet
        walletRepository.save(wallet);
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount.abs()); // Store absolute value
        transaction.setType(type);
        transaction.setDescription(request.getDescription());
        
        // Assess risk
        boolean isRisky = riskService.assessRisk(amount.abs(), wallet.getCurrency());
        transaction.setRiskFlag(isRisky);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Transaction created successfully: ID {}, Type: {}, Risk Flag: {}", 
                   savedTransaction.getId(), type, isRisky);
        
        return savedTransaction;
    }
    
    public List<Transaction> getUserTransactions(String email, String currency, Boolean riskFlag, String sort) {
        logger.info("Fetching transactions for user: {}", email);
        
        Wallet wallet = walletRepository.findAll().stream()
            .filter(w -> w.getUser().getEmail().equals(email))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Wallet> userWallets = walletRepository.findByUser(wallet.getUser());
        List<Transaction> transactions;
        
        // Apply sorting
        if ("amount".equalsIgnoreCase(sort)) {
            transactions = transactionRepository.findByWalletInOrderByAmountDesc(userWallets);
        } else {
            transactions = transactionRepository.findByWalletInOrderByCreatedAtDesc(userWallets);
        }
        
        // Apply filters
        if (currency != null && !currency.isEmpty()) {
            transactions = transactions.stream()
                .filter(t -> t.getWallet().getCurrency().equalsIgnoreCase(currency))
                .toList();
        }
        
        if (riskFlag != null) {
            transactions = transactions.stream()
                .filter(t -> t.getRiskFlag().equals(riskFlag))
                .toList();
        }
        
        return transactions;
    }
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}