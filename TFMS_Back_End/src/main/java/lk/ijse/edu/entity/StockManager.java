package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_managers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long StockManagerId;

    private String managerName;
    private String managerAddress;
    private String phoneNumber;
    private String basicSalary;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
