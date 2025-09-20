package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.AddNewStockDto;
import lk.ijse.edu.dto.StockResponseDto;
import lk.ijse.edu.entity.Stock;
import lk.ijse.edu.entity.TeaProduct;
import lk.ijse.edu.entity.TeaProductName;
import lk.ijse.edu.repository.StockRepository;
import lk.ijse.edu.repository.TeaProductRepository;
import lk.ijse.edu.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final TeaProductRepository teaProductRepository;

    private String generateNextStockId(String lastId) {
        if (lastId == null) return "ANS-000-001";

        String[] parts = lastId.split("-");
        int first = Integer.parseInt(parts[1]);
        int last = Integer.parseInt(parts[2]);

        last++;

        if (last > 999) {
            last = 1;
            first++;
        }

        if (first > 999) {
            throw new IllegalStateException("ID count has ended. Please contact the developer.");
        }

        return String.format("ANS-%03d-%03d", first, last);
    }

//    @Override
//    public List<StockResponseDto> getAllStockLevels() {
//        return stockRepository.findAll().stream()
//                .map(stock -> StockResponseDto.builder()
//                        .stockId(stock.getStockId())
//                        .productName(stock.getProduct().getName())
//                        .quantity(stock.getQuantity())
//                        .build())
//                .collect(Collectors.toList());
//    }

//    @Override
//    public String addTeaProduct(AddNewStockDto addNewStockDto) {
//        String lastId = stockRepository.findLastStockId();
//        String nextId = generateNextStockId(lastId);
//
//        TeaProductName teaProductName = TeaProductName.valueOf(addNewStockDto.getProductName());
//
//        TeaProduct teaProduct = teaProductRepository.findByName(teaProductName)
//                .orElseThrow(() -> new RuntimeException("Tea product not found: " + addNewStockDto.getProductName()));
//
//        Stock stock = Stock.builder()
//                .stockId(nextId)
//                .product(teaProduct)
//                .quantity(addNewStockDto.getQuantity() + " kg")
//                .dateTime(LocalDateTime.now())
//                .expiryDate(LocalDateTime.parse(addNewStockDto.getExpiryDate() + "T00:00:00"))
//                .notes(addNewStockDto.getNotes())
//                .build();
//
//        stockRepository.save(stock);
//        return "Stock saved successfully with ID " + nextId;
//    }
}
