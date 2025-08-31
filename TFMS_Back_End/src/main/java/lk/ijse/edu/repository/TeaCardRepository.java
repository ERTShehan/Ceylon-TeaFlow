package lk.ijse.edu.repository;

import lk.ijse.edu.entity.TeaCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeaCardRepository extends JpaRepository<TeaCard, String> {
    Optional<TeaCard> findByNumber(String number);
}
