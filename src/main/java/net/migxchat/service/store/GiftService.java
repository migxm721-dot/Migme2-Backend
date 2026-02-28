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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GiftService {

    private static final Logger log = LoggerFactory.getLogger(GiftService.class);

    @Autowired
    private StoreItemRepository storeItemRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private CreditService creditService;

    @Transactional
    public Purchase purchaseGift(String buyerId, Long itemId, List<String> recipients,
                                  String message, Boolean isPrivate, Boolean postToMiniblog) {
        StoreItem item = storeItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Store item not found: " + itemId));

        int recipientCount = (recipients != null && !recipients.isEmpty()) ? recipients.size() : 1;
        BigDecimal totalCost = item.getPrice().multiply(BigDecimal.valueOf(recipientCount));

        Credit credit = creditService.getBalance(buyerId);
        if (credit.getBalance().compareTo(totalCost) < 0) {
            throw new InsufficientCreditException("Insufficient credits to purchase gift");
        }

        creditService.deductCredit(buyerId, totalCost, "Gift purchase: " + item.getName());

        Purchase purchase = new Purchase();
        purchase.setBuyerId(buyerId);
        purchase.setItemId(itemId);
        purchase.setCost(totalCost);
        purchase.setQuantity(recipientCount);
        purchase.setRecipients(recipients != null ? String.join(",", recipients) : null);
        purchase.setMessage(message);
        purchase.setIsPrivate(isPrivate != null ? isPrivate : false);
        purchase.setPostToMiniblog(postToMiniblog != null ? postToMiniblog : false);
        purchase.setStatus("completed");
        return purchaseRepository.save(purchase);
    }
}
