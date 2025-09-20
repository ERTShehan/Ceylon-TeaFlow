package lk.ijse.edu.repository;

import lk.ijse.edu.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {
    @Query(value = "SELECT stock_id FROM stock ORDER BY stock_id DESC LIMIT 1", nativeQuery = true)
    String findLastStockId();
}
