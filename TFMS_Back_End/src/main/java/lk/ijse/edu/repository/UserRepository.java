package lk.ijse.edu.repository;

import lk.ijse.edu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    @Query(value = "SELECT id FROM users WHERE id REGEXP '^U-[0-9]{3}-[0-9]{3}$' ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findLastUserId();
}
