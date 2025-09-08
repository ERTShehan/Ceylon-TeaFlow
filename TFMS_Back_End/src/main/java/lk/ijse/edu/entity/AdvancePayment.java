package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "advance_payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdvancePayment {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private TeaLeafSupplier supplier;

    @Column(nullable = false)
    private String amount;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = true)
    private String reason;

    @Enumerated(EnumType.STRING)
    private AdvanceStatus status;
}
