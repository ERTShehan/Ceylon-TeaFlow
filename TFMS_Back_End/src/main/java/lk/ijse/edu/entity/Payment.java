package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    private String id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private String totalAmount;

    @CreationTimestamp
    private LocalDateTime paymentDate;
}
