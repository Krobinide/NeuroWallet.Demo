package com.neurowallet.controller;

import com.neurowallet.dto.WalletResponse;
import com.neurowallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
@CrossOrigin(origins = "*")
public class WalletController {
    
    @Autowired
    private WalletService walletService;
    
    @PostMapping
    public ResponseEntity<?> createWallet(@RequestBody Map<String, String> request, 
                                          Authentication authentication) {
        try {
            String email = authentication.getName();
            String currency = request.get("currency");
            WalletResponse wallet = walletService.createWallet(email, currency);
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getUserWallets(Authentication authentication) {
        try {
            String email = authentication.getName();
            List<WalletResponse> wallets = walletService.getUserWallets(email);
            return ResponseEntity.ok(wallets);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getWallet(@PathVariable Long id, 
                                        Authentication authentication) {
        try {
            String email = authentication.getName();
            WalletResponse wallet = walletService.getWalletById(id, email);
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateWallet(@PathVariable Long id,
                                          @RequestBody Map<String, String> request,
                                          Authentication authentication) {
        try {
            String email = authentication.getName();
            BigDecimal amount = new BigDecimal(request.get("amount"));
            WalletResponse wallet = walletService.updateWallet(id, amount, email);
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWallet(@PathVariable Long id, 
                                          Authentication authentication) {
        try {
            String email = authentication.getName();
            walletService.deleteWallet(id, email);
            return ResponseEntity.ok(Map.of("message", "Wallet deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}