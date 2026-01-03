package com.neurowallet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RiskService {
    
    private static final Logger logger = LoggerFactory.getLogger(RiskService.class);
    private static final BigDecimal RISK_THRESHOLD_MYR = new BigDecimal("5000.00");
    private static final BigDecimal RISK_THRESHOLD_SGD = new BigDecimal("1500.00");
    private static final BigDecimal RISK_THRESHOLD_USD = new BigDecimal("1200.00");
    
    public boolean assessRisk(BigDecimal amount, String currency) {
        logger.debug("Assessing risk for amount: {} {}", amount, currency);
        
        boolean isRisky = false;
        
        switch (currency.toUpperCase()) {
            case "MYR":
                isRisky = amount.compareTo(RISK_THRESHOLD_MYR) > 0;
                break;
            case "SGD":
                isRisky = amount.compareTo(RISK_THRESHOLD_SGD) > 0;
                break;
            case "USD":
                isRisky = amount.compareTo(RISK_THRESHOLD_USD) > 0;
                break;
            default:
                isRisky = false;
        }
        
        if (isRisky) {
            logger.warn("HIGH RISK TRANSACTION DETECTED: {} {}", amount, currency);
        }
        
        return isRisky;
    }
}