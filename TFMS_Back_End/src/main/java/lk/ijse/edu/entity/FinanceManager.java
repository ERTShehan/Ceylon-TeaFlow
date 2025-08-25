package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "finance_managers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinanceManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long financeManagerId;

    private String name;
    private String address;
    private String phoneNumber;
    private String basicSalary;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
