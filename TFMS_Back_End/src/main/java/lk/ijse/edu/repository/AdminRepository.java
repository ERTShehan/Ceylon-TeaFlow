package lk.ijse.edu.repository;

import lk.ijse.edu.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {
    @Query(value = "SELECT admin_id FROM ceos ORDER BY admin_id DESC LIMIT 1", nativeQuery = true)
    String findLastAdminId();
}
