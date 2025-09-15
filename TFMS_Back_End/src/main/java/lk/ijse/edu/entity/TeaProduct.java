package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeaProductName name;

    @Column(nullable = false)
    private double price;

    private String quantity;
    private String description;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> order_item;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Stock stock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeaPacketRequest> productRequests = new ArrayList<>();

}
