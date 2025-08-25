package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "tea_cards")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeaCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String number; // e.g., "TC-100001" (unique, created by CEO)

    @Column(nullable = false)
    private boolean used = false;

    @Temporal(TemporalType.TIMESTAMP)
    private Date issuedAt;
}
