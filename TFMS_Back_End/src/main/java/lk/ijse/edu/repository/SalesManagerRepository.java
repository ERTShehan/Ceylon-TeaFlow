package lk.ijse.edu.repository;

import jakarta.transaction.Transactional;
import lk.ijse.edu.entity.SalesManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesManagerRepository extends JpaRepository<SalesManager, String> {
    @Transactional
    @Modifying
    @Query(value = "UPDATE sales_managers SET status='Inactive' WHERE sales_manager_id = ?1",nativeQuery = true)
    void updateSalesManagerStatus(String id);

    @Query(value = "SELECT sales_manager_id FROM sales_managers ORDER BY sales_manager_id DESC LIMIT 1", nativeQuery = true)
    String findLastSalesManagerId();

    List<SalesManager> findSalesManagerByFullNameContainingIgnoreCase(String keyword);
}
