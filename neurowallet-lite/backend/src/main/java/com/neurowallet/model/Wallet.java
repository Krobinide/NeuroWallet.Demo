package com.neurowallet.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
public class Wallet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String currency; // MYR, SGD, USD
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus status;
    
    public enum WalletStatus {
        ACTIVE,
        FROZEN
    }
    
    // Constructors
    public Wallet() {
        this.balance = BigDecimal.ZERO;
        this.status = WalletStatus.ACTIVE;
    }
    
    public Wallet(User user, String currency) {
        this.user = user;
        this.currency = currency;
        this.balance = BigDecimal.ZERO;
        this.status = WalletStatus.ACTIVE;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
    
    public WalletStatus getStatus() {
        return status;
    }
    
    public void setStatus(WalletStatus status) {
        this.status = status;
    }
}