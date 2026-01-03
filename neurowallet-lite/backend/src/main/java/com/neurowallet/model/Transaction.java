package com.neurowallet.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    @Column(name = "risk_flag", nullable = false)
    private Boolean riskFlag;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(length = 500)
    private String description;
    
    public enum TransactionType {
        TRANSFER,
        CONVERSION,
        DEPOSIT,
        WITHDRAWAL
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (riskFlag == null) {
            riskFlag = false;
        }
    }
    
    // Constructors
    public Transaction() {
    }
    
    public Transaction(Wallet wallet, BigDecimal amount, TransactionType type) {
        this.wallet = wallet;
        this.amount = amount;
        this.type = type;
        this.riskFlag = false;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Wallet getWallet() {
        return wallet;
    }
    
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    public Boolean getRiskFlag() {
        return riskFlag;
    }
    
    public void setRiskFlag(Boolean riskFlag) {
        this.riskFlag = riskFlag;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}