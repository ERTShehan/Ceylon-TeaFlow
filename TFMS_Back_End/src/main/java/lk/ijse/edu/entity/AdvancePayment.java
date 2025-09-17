package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    @Column(nullable = true)
    private String reason;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
