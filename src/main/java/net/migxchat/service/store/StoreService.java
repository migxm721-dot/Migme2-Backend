package net.migxchat.service.store;

import net.migxchat.exception.InsufficientCreditException;
import net.migxchat.model.credit.Credit;
import net.migxchat.model.store.Purchase;
import net.migxchat.model.store.StoreItem;
import net.migxchat.repository.PurchaseRepository;
import net.migxchat.repository.StoreItemRepository;
import net.migxchat.service.credit.CreditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreService {

    private static final Logger log = LoggerFactory.getLogger(StoreService.class);

    @Autowired
    private StoreItemRepository storeItemRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private CreditService creditService;

    public Page<StoreItem> getFeaturedItems(Pageable pageable) {
        return storeItemRepository.findByIsFeaturedTrueAndIsActiveTrue(pageable);
    }

    public Page<StoreItem> getItemsByTypeAndCategory(String type, Integer categoryId, Pageable pageable) {
        if (categoryId != null) {
            return storeItemRepository.findByTypeAndCategoryIdAndIsActiveTrue(type, categoryId, pageable);
        }
        return storeItemRepository.findByTypeAndIsActiveTrue(type, pageable);
    }

    public StoreItem getStoreItemById(Long itemId) {
        return storeItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Store item not found: " + itemId));
    }

    @Transactional
    public Purchase purchaseStickerPack(String userId, Long packId) {
        StoreItem item = getStoreItemById(packId);
        Credit credit = creditService.getBalance(userId);

        if (credit.getBalance().compareTo(item.getPrice()) < 0) {
            throw new InsufficientCreditException("Insufficient credits to purchase sticker pack");
        }

        creditService.deductCredit(userId, item.getPrice(), "Purchase: " + item.getName());

        Purchase purchase = new Purchase();
        purchase.setBuyerId(userId);
        purchase.setItemId(packId);
        purchase.setCost(item.getPrice());
        purchase.setQuantity(1);
        purchase.setStatus("completed");
        return purchaseRepository.save(purchase);
    }
}
