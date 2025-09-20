package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Stock {
    @Id
    private String stockId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeaProductName name;

    private String quantity;

    private LocalDateTime dateTime;
    private LocalDateTime expiryDate;
    private String notes;

    @Column(nullable = false)
    private String type; // "INCOMING" or "OUTGOING"
}
