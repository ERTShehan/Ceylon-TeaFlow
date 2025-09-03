package lk.ijse.edu.repository;

import lk.ijse.edu.entity.TeaCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeaCardRepository extends JpaRepository<TeaCard, String> {
    Optional<TeaCard> findByNumber(String number);

    boolean existsByNumber(String number);

    @Query(value = "SELECT id FROM tea_cards ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findLastTeaCardId();

    List<TeaCard> findTeaCardByNameContainingIgnoreCase(String keyword);
}
