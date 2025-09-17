package lk.ijse.edu.repository;

import lk.ijse.edu.entity.AdvancePayment;
import lk.ijse.edu.entity.TeaLeafSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvancePaymentRepository extends JpaRepository<AdvancePayment, String> {
    @Query(value = "SELECT id FROM advance_payments ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findLastAdvancePaymentId();

    List<AdvancePayment> findBySupplierOrderByPaymentDateDesc(TeaLeafSupplier supplier);

    @Query("SELECT COALESCE(SUM(CAST(a.amount AS double)), 0) " +
            "FROM AdvancePayment a " +
            "WHERE a.supplier.supplierId = :supplierId " +
            "AND FUNCTION('YEAR', a.paymentDate) = :year " +
            "AND FUNCTION('MONTH', a.paymentDate) = :month " +
            "AND a.status = 'APPROVED'")
    double getTotalAdvanceBySupplierAndMonth(@Param("supplierId") String supplierId,
                                             @Param("year") int year,
                                             @Param("month") int month);
}
