package net.migxchat.controller.touch.sites.account;

import net.migxchat.dto.request.TransferCreditRequest;
import net.migxchat.dto.response.TransactionResponse;
import net.migxchat.model.credit.Credit;
import net.migxchat.model.credit.Transaction;
import net.migxchat.service.credit.CreditService;
import net.migxchat.service.credit.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sites/touch/account")
public class CreditController {

    @Autowired
    private CreditService creditService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/home")
    public ResponseEntity<?> getAccountHome(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        Credit credit = creditService.getBalance(userId);
        return ResponseEntity.ok(Map.of("userId", userId, "balance", credit.getBalance(),
                "totalEarned", credit.getTotalEarned(), "totalSpent", credit.getTotalSpent()));
    }

    @GetMapping("/recharge_credit")
    public ResponseEntity<?> getRechargePage(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        Credit credit = creditService.getBalance(userId);
        return ResponseEntity.ok(Map.of("userId", userId, "currentBalance", credit.getBalance(),
                "paymentMethods", List.of("card", "paypal", "bank_transfer")));
    }

    @PostMapping("/recharge")
    public ResponseEntity<?> recharge(@RequestBody Map<String, Object> body,
                                       @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        String paymentMethod = (String) body.get("paymentMethod");
        String transactionId = (String) body.getOrDefault("transactionId", "");

        Credit credit = creditService.rechargeCredit(userId, amount, paymentMethod, transactionId);
        return ResponseEntity.ok(Map.of("newBalance", credit.getBalance(), "message", "Credit recharged successfully"));
    }

    @GetMapping("/transfer_credit")
    public ResponseEntity<?> getTransferPage(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        Credit credit = creditService.getBalance(userId);
        return ResponseEntity.ok(Map.of("userId", userId, "availableBalance", credit.getBalance()));
    }

    @PostMapping("/transfer_credit")
    public ResponseEntity<?> transferCredit(@RequestBody TransferCreditRequest request,
                                             @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        Transaction transaction = creditService.transferCredit(userId, request.getRecipientUsername(),
                request.getAmount(), request.getMessage());
        return ResponseEntity.ok(TransactionResponse.from(transaction));
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(@RequestHeader(value = "X-User-Id", required = false) String userId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        Page<Transaction> transactions = transactionService.getUserTransactions(userId, PageRequest.of(page, size));
        List<TransactionResponse> responses = transactions.getContent().stream()
                .map(TransactionResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("transactions", responses, "total", transactions.getTotalElements(), "page", page));
    }
}
