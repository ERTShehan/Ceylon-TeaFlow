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
    private String StockManagerId;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String basicSalary;
    private String status;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
