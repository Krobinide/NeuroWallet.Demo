package com.neurowallet.dto;

import java.math.BigDecimal;

public class WalletResponse {
    
    private Long id;
    private Long userId;
    private String currency;
    private BigDecimal balance;
    private String status;
    
    // Constructors
    public WalletResponse() {
    }
    
    public WalletResponse(Long id, Long userId, String currency, BigDecimal balance, String status) {
        this.id = id;
        this.userId = userId;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}