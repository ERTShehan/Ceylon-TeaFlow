package lk.ijse.edu.repository;

import lk.ijse.edu.entity.TeaLeafPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddTeaLeafPriceRepository extends JpaRepository<TeaLeafPrice, String> {
    @Query(value = "SELECT tea_leaf_price_id FROM tea_leaf_price ORDER BY tea_leaf_price_id DESC LIMIT 1", nativeQuery = true)
    String findLastTeaLeafPriceId();
    Optional<TeaLeafPrice> findByEffectiveMonth(String effectiveMonth);
}
