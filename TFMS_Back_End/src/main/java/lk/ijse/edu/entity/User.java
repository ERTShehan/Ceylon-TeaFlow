package lk.ijse.edu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private SystemUserRole role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private NormalCustomer normalCustomer;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private TeaLeafSupplier teaLeafSupplier;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private TeaMaker teaMaker;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private StockManager stockManager;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SalesManager salesManager;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private FinanceManager financeManager;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Admin admin;
}