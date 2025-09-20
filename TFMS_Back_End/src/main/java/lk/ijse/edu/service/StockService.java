package lk.ijse.edu.service;

import lk.ijse.edu.dto.AddNewStockDto;
import lk.ijse.edu.dto.StockHistoryDto;
import lk.ijse.edu.dto.StockResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StockService {
    List<StockResponseDto> getGroupedStockLevels();
    String addNewStock(AddNewStockDto dto);
    Page<StockHistoryDto> getStockHistory(int page, int size, String filter);
    Long getTotalStockQuantity();
}
