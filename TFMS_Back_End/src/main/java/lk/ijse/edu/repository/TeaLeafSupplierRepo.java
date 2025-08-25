package lk.ijse.edu.repository;

import lk.ijse.edu.entity.TeaLeafSupplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeaLeafSupplierRepo extends JpaRepository<TeaLeafSupplier, Long> {
    boolean existsByEmail(String email);
}
