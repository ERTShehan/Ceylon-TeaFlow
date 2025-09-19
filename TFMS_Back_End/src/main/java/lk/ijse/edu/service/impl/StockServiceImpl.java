package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.StockResponseDto;
import lk.ijse.edu.repository.StockRepository;
import lk.ijse.edu.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;

    @Override
    public List<StockResponseDto> getAllStockLevels() {
        return stockRepository.findAll().stream()
                .map(stock -> StockResponseDto.builder()
                        .stockId(stock.getStockId())
                        .productName(stock.getProduct().getName())
                        .quantity(stock.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }
}
