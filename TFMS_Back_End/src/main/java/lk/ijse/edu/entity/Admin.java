package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ceos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {
    @Id
    private String adminId;

    private String name;
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
