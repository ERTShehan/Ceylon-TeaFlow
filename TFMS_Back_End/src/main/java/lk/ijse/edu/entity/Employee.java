package lk.ijse.edu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "employees")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    private String address;
    private String basicSalary;
    private String phone;
    private String department;
}
