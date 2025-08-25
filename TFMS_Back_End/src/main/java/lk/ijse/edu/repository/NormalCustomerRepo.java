package lk.ijse.edu.repository;

import lk.ijse.edu.entity.NormalCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NormalCustomerRepo extends JpaRepository<NormalCustomer, Long> {
    boolean existsByEmail(String email);
}
