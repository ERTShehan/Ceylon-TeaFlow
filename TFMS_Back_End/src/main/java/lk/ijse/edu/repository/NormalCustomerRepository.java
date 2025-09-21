package lk.ijse.edu.repository;

import lk.ijse.edu.entity.NormalCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NormalCustomerRepository extends JpaRepository<NormalCustomer, String> {
    boolean existsByEmail(String email);

    @Query(value = "SELECT customer_id FROM normal_customer ORDER BY customer_id DESC LIMIT 1", nativeQuery = true)
    String findLastCustomerId();

    Optional<NormalCustomer> findByUserUsername(String username);
}
