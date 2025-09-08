package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_card")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCard {
    @Id
    private String id;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "card_holder", nullable = false)
    private String cardHolder;

    @Column(name = "expiry_date", nullable = false)
    private String expiryDate;

    @Column(nullable = false)
    private String cvv;

    @OneToMany(mappedBy = "order_card", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
}
