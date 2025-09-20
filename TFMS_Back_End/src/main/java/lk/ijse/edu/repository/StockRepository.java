package lk.ijse.edu.repository;

import lk.ijse.edu.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {
    @Query(value = "SELECT stock_id FROM stock ORDER BY stock_id DESC LIMIT 1", nativeQuery = true)
    String findLastStockId();

//    Optional<Stock> findByName(TeaProductName name);

    @Query("SELECT s.name, SUM(CAST(s.quantity AS int)) FROM Stock s GROUP BY s.name")
    List<Object[]> findGroupedStockLevels();
}
