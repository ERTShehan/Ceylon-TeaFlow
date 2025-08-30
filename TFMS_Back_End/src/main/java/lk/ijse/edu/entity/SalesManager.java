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
    private String SalesManagerId;

    private String fullName;
    private String address;
    private String phoneNumber;
    private String basicSalary;
    private String status;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
