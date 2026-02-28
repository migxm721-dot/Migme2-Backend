package net.migxchat.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseGiftRequest {
    private Long itemId;
    private List<String> recipients;
    private String message;
    private Boolean isPrivate;
    private Boolean postToMiniblog;
}
