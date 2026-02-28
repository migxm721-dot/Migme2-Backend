package net.migxchat.controller.touch.sites.store;

import net.migxchat.dto.request.PurchaseGiftRequest;
import net.migxchat.dto.response.StoreItemResponse;
import net.migxchat.model.store.Purchase;
import net.migxchat.model.store.StoreItem;
import net.migxchat.service.store.GiftService;
import net.migxchat.service.store.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sites/touch/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private GiftService giftService;

    @GetMapping("/home_touch")
    public ResponseEntity<?> getStoreHome(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        Page<StoreItem> featured = storeService.getFeaturedItems(PageRequest.of(page, size));
        List<StoreItemResponse> items = featured.getContent().stream()
                .map(StoreItemResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("featured", items, "total", featured.getTotalElements()));
    }

    @GetMapping("/show_with_category_touch")
    public ResponseEntity<?> getItemsByCategory(@RequestParam(required = false) String type,
                                                 @RequestParam(required = false) Integer categoryId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size) {
        Page<StoreItem> items = storeService.getItemsByTypeAndCategory(type, categoryId, PageRequest.of(page, size));
        List<StoreItemResponse> responses = items.getContent().stream()
                .map(StoreItemResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("items", responses, "total", items.getTotalElements(), "page", page));
    }

    @PostMapping("/purchase_gift")
    public ResponseEntity<?> purchaseGift(@RequestBody PurchaseGiftRequest request,
                                           @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        Purchase purchase = giftService.purchaseGift(userId, request.getItemId(), request.getRecipients(),
                request.getMessage(), request.getIsPrivate(), request.getPostToMiniblog());
        return ResponseEntity.ok(Map.of("purchaseId", purchase.getId(), "status", purchase.getStatus(),
                "cost", purchase.getCost()));
    }

    @PostMapping("/purchase_sticker")
    public ResponseEntity<?> purchaseSticker(@RequestBody Map<String, Object> body,
                                              @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID required"));
        }
        if (body.get("packId") == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "packId is required"));
        }
        Long packId;
        try {
            packId = Long.valueOf(body.get("packId").toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid packId"));
        }
        Purchase purchase = storeService.purchaseStickerPack(userId, packId);
        return ResponseEntity.ok(Map.of("purchaseId", purchase.getId(), "status", purchase.getStatus(),
                "cost", purchase.getCost()));
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> getItem(@PathVariable Long itemId) {
        StoreItem item = storeService.getStoreItemById(itemId);
        return ResponseEntity.ok(StoreItemResponse.from(item));
    }
}
