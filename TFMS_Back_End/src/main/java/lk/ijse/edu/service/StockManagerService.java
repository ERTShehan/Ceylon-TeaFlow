package lk.ijse.edu.service;

import lk.ijse.edu.dto.StockManagerDto;

public interface StockManagerService {
    String saveStockManager(StockManagerDto stockManagerDto);
    String updateStockManager(StockManagerDto stockManagerDto);
}
