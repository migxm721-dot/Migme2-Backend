package net.migxchat.model.credit;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String type;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "from_user_id", length = 50)
    private String fromUserId;

    @Column(name = "to_user_id", length = 50)
    private String toUserId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(length = 20)
    private String status = "completed";

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime timestamp;
}
