package lk.ijse.edu.repository;

import jakarta.transaction.Transactional;
import lk.ijse.edu.entity.StockManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockManagerRepository extends JpaRepository<StockManager, String> {
    @Transactional
    @Modifying
    @Query(value = "UPDATE stock_managers SET status='Inactive' WHERE stock_manager_id = ?1",nativeQuery = true)
    void updateStockManagerStatus(String id);

    @Query(value = "SELECT stock_manager_id FROM stock_managers ORDER BY stock_manager_id DESC LIMIT 1", nativeQuery = true)
    String findLastStockManagerId();

    List<StockManager> findStockManagerByFullNameContainingIgnoreCase(String keyword);
}
