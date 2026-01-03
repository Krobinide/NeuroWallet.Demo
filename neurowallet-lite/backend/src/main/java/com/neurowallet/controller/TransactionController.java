package com.neurowallet.controller;

import com.neurowallet.dto.TransactionRequest;
import com.neurowallet.model.Transaction;
import com.neurowallet.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionRequest request,
                                                Authentication authentication) {
        try {
            String email = authentication.getName();
            Transaction transaction = transactionService.createTransaction(request, email);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getUserTransactions(
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) Boolean risk,
            @RequestParam(required = false) String sort,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            List<Transaction> transactions = transactionService.getUserTransactions(
                email, currency, risk, sort
            );
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}