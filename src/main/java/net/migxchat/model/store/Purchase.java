package net.migxchat.model.store;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "buyer_id", nullable = false, length = 50)
    private String buyerId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "recipients", columnDefinition = "TEXT")
    private String recipients;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_private")
    private Boolean isPrivate = false;

    @Column(name = "post_to_miniblog")
    private Boolean postToMiniblog = false;

    @Column(length = 20)
    private String status = "completed";

    @CreationTimestamp
    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;
}
