package lk.ijse.edu.repository;

import jakarta.transaction.Transactional;
import lk.ijse.edu.entity.TeaMaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeaMakerRepository extends JpaRepository<TeaMaker,Long> {
//    @Transactional

}
