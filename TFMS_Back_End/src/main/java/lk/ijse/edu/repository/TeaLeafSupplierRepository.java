package lk.ijse.edu.repository;

import lk.ijse.edu.entity.TeaLeafSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TeaLeafSupplierRepository extends JpaRepository<TeaLeafSupplier, String> {
    boolean existsByEmail(String email);

    @Query(value = "SELECT supplier_id FROM tea_leaf_supplier ORDER BY supplier_id DESC LIMIT 1", nativeQuery = true)
    String findLastSupplierId();
}
