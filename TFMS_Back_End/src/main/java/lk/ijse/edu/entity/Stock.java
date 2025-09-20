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

//    @OneToOne
//    @JoinColumn(name = "product_id", nullable = false, unique = true)
//    private TeaProduct product;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockTeaProduct> stockTeaProducts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeaProductName name;

    private String quantity;

//    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<StockManager> stockManagers = new ArrayList<>();

    private LocalDateTime dateTime;
    private LocalDateTime expiryDate;
    private String notes;
}
