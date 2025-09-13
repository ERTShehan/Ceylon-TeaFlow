package lk.ijse.edu.repository;

import lk.ijse.edu.entity.TeaLeafCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeaCountRepository extends JpaRepository<TeaLeafCount, String> {
    @Query("SELECT t FROM TeaLeafCount t WHERE t.date = :today")
    List<TeaLeafCount> findAllByToday(@Param("today") String today);

    @Query(value = "SELECT id FROM tea_leaf_count ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findLastTeaLeafCountId();

    List<TeaLeafCount> findByDate(String date);

    @Query(value = """
            SELECT 
                s.supplier_id AS supplierId,
                CONCAT(s.first_name, ' ', s.last_name) AS supplierName,
                s.tea_card_number AS teaCardNumber,
                SUM(CAST(t.net_weight AS DECIMAL(10,2))) AS totalSupplied
            FROM tea_leaf_count t
            JOIN tea_leaf_supplier s ON t.supplier_id = s.supplier_id
            WHERE STR_TO_DATE(t.date, '%Y-%m-%d') >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
            GROUP BY s.supplier_id, s.first_name, s.last_name, s.tea_card_number
            ORDER BY totalSupplied DESC
            LIMIT 5
            """, nativeQuery = true)
    List<Object[]> findTopSuppliersNative();
}
