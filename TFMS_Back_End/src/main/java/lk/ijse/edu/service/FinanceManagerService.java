package lk.ijse.edu.service;

import lk.ijse.edu.dto.FinanceManagerDto;

import java.util.List;

public interface FinanceManagerService {
    String saveFinanceManager(FinanceManagerDto financeManagerDto);
    String updateFinanceManager(FinanceManagerDto financeManagerDto);
    void deleteFinanceManager(String id);
    List<FinanceManagerDto> getAllFinanceManagers();
    List<FinanceManagerDto> searchFinanceManager(String keyword);
    void changeFinanceManagerStatus(String id);
}
