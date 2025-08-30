package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tea_makers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeaMaker {
    @Id
    private String teaMakerId;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String basicSalary;
    private String status;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
