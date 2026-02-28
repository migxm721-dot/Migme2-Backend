package net.migxchat.dto.response;

import lombok.Data;
import net.migxchat.model.store.StoreItem;

import java.math.BigDecimal;

@Data
public class StoreItemResponse {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Integer categoryId;
    private BigDecimal price;
    private String imageUrl;
    private String hotkey;
    private Integer requiredLevel;
    private Integer popularity;
    private Boolean isFeatured;

    public static StoreItemResponse from(StoreItem item) {
        StoreItemResponse r = new StoreItemResponse();
        r.setId(item.getId());
        r.setName(item.getName());
        r.setDescription(item.getDescription());
        r.setType(item.getType());
        r.setCategoryId(item.getCategoryId());
        r.setPrice(item.getPrice());
        r.setImageUrl(item.getImageUrl());
        r.setHotkey(item.getHotkey());
        r.setRequiredLevel(item.getRequiredLevel());
        r.setPopularity(item.getPopularity());
        r.setIsFeatured(item.getIsFeatured());
        return r;
    }
}
