package lk.ijse.edu.service;

import lk.ijse.edu.dto.AddNewStockDto;
import lk.ijse.edu.dto.StockResponseDto;

import java.util.List;

public interface StockService {
    List<StockResponseDto> getAllStockLevels();
    String addTeaProduct(AddNewStockDto addNewStockDto);
}
