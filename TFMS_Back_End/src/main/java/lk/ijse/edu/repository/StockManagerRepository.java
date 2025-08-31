package lk.ijse.edu.repository;

import lk.ijse.edu.entity.StockManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockManagerRepository extends JpaRepository<StockManager, String> {
    @Query(value = "SELECT stock_manager_id FROM stock_managers ORDER BY stock_manager_id DESC LIMIT 1", nativeQuery = true)
    String findLastStockManagerId();
}
