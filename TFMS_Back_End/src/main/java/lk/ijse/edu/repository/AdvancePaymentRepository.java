package lk.ijse.edu.repository;

import lk.ijse.edu.entity.AdvancePayment;
import lk.ijse.edu.entity.TeaLeafSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvancePaymentRepository extends JpaRepository<AdvancePayment, String> {
    @Query(value = "SELECT id FROM advance_payments ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findLastAdvancePaymentId();

    List<AdvancePayment> findBySupplierOrderByPaymentDateDesc(TeaLeafSupplier supplier);
}
