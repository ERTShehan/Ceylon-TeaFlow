package lk.ijse.edu.repository;

import lk.ijse.edu.entity.TeaProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeaProductRepository extends JpaRepository<TeaProduct, String> {
    boolean existsByName(String name);

    @Query(value = "SELECT id FROM tea_products ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findLastProductId();

    List<TeaProduct> findTeaProductByNameContainingIgnoreCase(String keyword);
}
