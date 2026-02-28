package net.migxchat.dto.response;

import lombok.Data;
import net.migxchat.model.credit.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private String type;
    private BigDecimal amount;
    private String fromUserId;
    private String toUserId;
    private String description;
    private String paymentMethod;
    private String status;
    private LocalDateTime timestamp;

    public static TransactionResponse from(Transaction transaction) {
        TransactionResponse r = new TransactionResponse();
        r.setId(transaction.getId());
        r.setType(transaction.getType());
        r.setAmount(transaction.getAmount());
        r.setFromUserId(transaction.getFromUserId());
        r.setToUserId(transaction.getToUserId());
        r.setDescription(transaction.getDescription());
        r.setPaymentMethod(transaction.getPaymentMethod());
        r.setStatus(transaction.getStatus());
        r.setTimestamp(transaction.getTimestamp());
        return r;
    }
}
