package lk.ijse.edu.repository;

import lk.ijse.edu.entity.TeaCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeaCardRepo extends JpaRepository<TeaCard, Long> {
    Optional<TeaCard> findByNumber(String number);
}
