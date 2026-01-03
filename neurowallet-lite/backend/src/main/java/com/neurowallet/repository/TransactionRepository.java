package com.neurowallet.repository;

import com.neurowallet.model.Transaction;
import com.neurowallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWallet(Wallet wallet);
    
    List<Transaction> findByWalletIn(List<Wallet> wallets);
    List<Transaction> findByRiskFlag(Boolean riskFlag);
    
    @Query("SELECT t FROM Transaction t WHERE t.wallet.user.id = :userId")
    List<Transaction> findByUserId(Long userId);
    
    @Query("SELECT t FROM Transaction t WHERE t.wallet.currency = :currency")
    List<Transaction> findByCurrency(String currency);
    
    List<Transaction> findByWalletInOrderByAmountDesc(List<Wallet> wallets);
    List<Transaction> findByWalletInOrderByCreatedAtDesc(List<Wallet> wallets);
}