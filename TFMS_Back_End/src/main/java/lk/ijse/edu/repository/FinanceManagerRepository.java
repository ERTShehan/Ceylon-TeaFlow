package lk.ijse.edu.repository;

import jakarta.transaction.Transactional;
import lk.ijse.edu.entity.FinanceManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanceManagerRepository extends JpaRepository<FinanceManager, String> {
    @Transactional
    @Modifying
    @Query("UPDATE FinanceManager fm SET fm.status = CASE WHEN fm.status = 'Active' THEN 'Inactive' ELSE 'Active' END WHERE fm.financeManagerId = ?1")
    void updateFinanceManagerStatus(String id);

    @Query(value = "SELECT finance_manager_id FROM finance_managers ORDER BY finance_manager_id DESC LIMIT 1", nativeQuery = true)
    String findLastFinanceManagerId();

    List<FinanceManager> findFinanceManagerByFullNameContainingIgnoreCase(String keyword);
}
