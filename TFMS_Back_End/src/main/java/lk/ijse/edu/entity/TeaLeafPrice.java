package lk.ijse.edu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "tea_leaf_price")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeaLeafPrice {
    @Id
    private String teaLeafPriceId;
    private double pricePerKg;
    private String effectiveMonth;
}
