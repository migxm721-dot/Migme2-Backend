package net.migxchat.repository;

import net.migxchat.model.store.StoreItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreItemRepository extends JpaRepository<StoreItem, Long> {

    Page<StoreItem> findByIsFeaturedTrueAndIsActiveTrue(Pageable pageable);

    Page<StoreItem> findByTypeAndCategoryIdAndIsActiveTrue(String type, Integer categoryId, Pageable pageable);

    Page<StoreItem> findByTypeAndIsActiveTrue(String type, Pageable pageable);
}
