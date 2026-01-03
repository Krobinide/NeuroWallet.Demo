package com.neurowallet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransactionRequest {
    
    @NotNull(message = "Wallet ID is required")
    private Long walletId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Transaction type is required")
    private String type; // TRANSFER, DEPOSIT, WITHDRAWAL
    
    private Long toWalletId; // Required for TRANSFER
    
    private String description;
    
    // Constructors
    public TransactionRequest() {
    }
    
    public TransactionRequest(Long walletId, BigDecimal amount, String type) {
        this.walletId = walletId;
        this.amount = amount;
        this.type = type;
    }
    
    // Getters and Setters
    public Long getWalletId() {
        return walletId;
    }
    
    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getToWalletId() {
        return toWalletId;
    }
    
    public void setToWalletId(Long toWalletId) {
        this.toWalletId = toWalletId;
    }
}