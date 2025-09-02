package lk.ijse.edu.service;

import lk.ijse.edu.dto.StockManagerDto;

import java.util.List;

public interface StockManagerService {
    String saveStockManager(StockManagerDto stockManagerDto);
    String updateStockManager(StockManagerDto stockManagerDto);
    void deleteStockManager(String id);
    List<StockManagerDto> getAllStockManagers();
    List<StockManagerDto> searchStockManager(String keyword);
    void changeStockManagerStatus(String id);
}
