package lk.ijse.edu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "tea_products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeaProduct {
    @Id
    private String id;
    private String name;

    @Column(nullable = false)
    private double price;

    private String quantity;
    private String description;
}
