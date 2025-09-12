package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeaLeafSupplier {
    @Id
    private String supplierId;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;

    private String address;
    private String teaCardNumber;

    @OneToOne(mappedBy = "supplier", cascade = CascadeType.ALL)
    private TeaCard teaCard;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private List<AdvancePayment> advancePayments = new ArrayList<>();

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private List<TeaLeafCount> teaLeafCounts = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
