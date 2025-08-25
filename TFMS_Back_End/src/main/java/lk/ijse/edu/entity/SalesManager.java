package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sales_managers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long SalesManagerId;

    private String name;
    private String address;
    private String phoneNumber;
    private String basicSalary;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
