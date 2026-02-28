package net.migxchat.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferCreditRequest {
    private String recipientUsername;
    private BigDecimal amount;
    private String message;
}
