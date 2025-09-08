package lk.ijse.edu.repository;

import lk.ijse.edu.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    @Query(value = "SELECT id FROM employees ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findLastEmployeeId();

    List<Employee> findEmployeeByNameContainingIgnoreCase(String keyword);
}
