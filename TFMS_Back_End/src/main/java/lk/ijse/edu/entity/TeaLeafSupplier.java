package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
