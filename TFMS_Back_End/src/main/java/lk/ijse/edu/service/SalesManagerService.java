package lk.ijse.edu.service;

import lk.ijse.edu.dto.SalesManagerDto;

import java.util.List;

public interface SalesManagerService {
    String saveSalesManager(SalesManagerDto salesManagerDto);
    String updateSalesManager(SalesManagerDto salesManagerDto);
    void deleteSalesManager(String id);
    List<SalesManagerDto> getAllSalesManagers();
    List<SalesManagerDto> searchSalesManager(String keyword);
    void changeSalesManagerStatus(String id);
}
