package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Expenses {
    @Id
    private String expenseId;
    private String description;
    private double amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private TeaPacketRequest request;

    @Enumerated(EnumType.STRING)
    private ExpensesType type;
}
