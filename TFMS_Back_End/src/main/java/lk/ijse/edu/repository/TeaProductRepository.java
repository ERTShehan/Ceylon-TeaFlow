package lk.ijse.edu.repository;

import lk.ijse.edu.entity.TeaProduct;
import lk.ijse.edu.entity.TeaProductName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeaProductRepository extends JpaRepository<TeaProduct, String> {
    boolean existsByName(TeaProductName name);

    @Query(value = "SELECT id FROM tea_products ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findLastProductId();

    @Query("SELECT t FROM TeaProduct t WHERE LOWER(CAST(t.name AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<TeaProduct> searchByNameContainingIgnoreCase(@Param("keyword") String keyword);

    Optional<TeaProduct> findByName(TeaProductName name);

//    List<TeaProduct> findTeaProductByNameContainingIgnoreCase(String keyword);
}
