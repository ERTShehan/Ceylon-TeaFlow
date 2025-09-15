package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "tea_packet_request")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeaPacketRequest {
    @Id
    private String requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private TeaLeafSupplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private TeaProduct product;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Date requestDate;

    @OneToOne(mappedBy = "request", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Expenses expenses;
}
