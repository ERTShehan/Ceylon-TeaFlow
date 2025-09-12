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

}
