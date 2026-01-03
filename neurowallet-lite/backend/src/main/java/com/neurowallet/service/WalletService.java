package com.neurowallet.service;

import com.neurowallet.dto.WalletResponse;
import com.neurowallet.model.User;
import com.neurowallet.model.Wallet;
import com.neurowallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletService {
    
    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private UserService userService;
    
    public WalletResponse createWallet(String email, String currency) {
        logger.info("Creating wallet for user: {} with currency: {}", email, currency);
        
        User user = userService.getUserByEmail(email);
        
        // Check if wallet already exists
        if (walletRepository.findByUserAndCurrency(user, currency).isPresent()) {
            throw new RuntimeException("Wallet already exists for this currency");
        }
        
        Wallet wallet = new Wallet(user, currency);
        Wallet savedWallet = walletRepository.save(wallet);
        
        logger.info("Wallet created successfully: ID {}", savedWallet.getId());
        return convertToResponse(savedWallet);
    }
    
    public List<WalletResponse> getUserWallets(String email) {
        User user = userService.getUserByEmail(email);
        List<Wallet> wallets = walletRepository.findByUser(user);
        return wallets.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public WalletResponse getWalletById(Long id, String email) {
        Wallet wallet = walletRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        // Verify ownership
        if (!wallet.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access to wallet");
        }
        
        return convertToResponse(wallet);
    }
    
    public WalletResponse updateWallet(Long id, BigDecimal amount, String email) {
        logger.info("Updating wallet ID: {} with amount: {}", id, amount);
        
        Wallet wallet = walletRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        // Verify ownership
        if (!wallet.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access to wallet");
        }
        
        wallet.setBalance(wallet.getBalance().add(amount));
        Wallet updatedWallet = walletRepository.save(wallet);
        
        logger.info("Wallet updated successfully: ID {}", updatedWallet.getId());
        return convertToResponse(updatedWallet);
    }
    
    public void deleteWallet(Long id, String email) {
        Wallet wallet = walletRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        // Verify ownership
        if (!wallet.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized access to wallet");
        }
        
        walletRepository.delete(wallet);
        logger.info("Wallet deleted: ID {}", id);
    }
    
    public WalletResponse freezeWallet(Long id) {
        Wallet wallet = walletRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        wallet.setStatus(Wallet.WalletStatus.FROZEN);
        Wallet updatedWallet = walletRepository.save(wallet);
        
        logger.info("Wallet frozen: ID {}", id);
        return convertToResponse(updatedWallet);
    }
    
    public WalletResponse unfreezeWallet(Long id) {
        Wallet wallet = walletRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        wallet.setStatus(Wallet.WalletStatus.ACTIVE);
        Wallet updatedWallet = walletRepository.save(wallet);
        
        logger.info("Wallet unfrozen: ID {}", id);
        return convertToResponse(updatedWallet);
    }
    
    private WalletResponse convertToResponse(Wallet wallet) {
        return new WalletResponse(
            wallet.getId(),
            wallet.getUser().getId(),
            wallet.getCurrency(),
            wallet.getBalance(),
            wallet.getStatus().name()
        );
    }
}