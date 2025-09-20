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
import java.util.Optional;
import java.util.UUID;

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

    @Override
    public List<StockResponseDto> getAllStockLevels() {
        return stockRepository.findAll().stream().map(stock ->
                new StockResponseDto(
                        stock.getStockId(),
                        stock.getName().name(),
                        stock.getQuantity(),
                        stock.getExpiryDate() != null ? stock.getExpiryDate().toString() : null,
                        stock.getNotes()
                )
        ).toList();
    }

    @Override
    public String addTeaProduct(AddNewStockDto dto) {
        String lastId = stockRepository.findLastStockId();
        String newId = generateNextStockId(lastId);

        TeaProductName teaName = TeaProductName.valueOf(dto.getProductName());

        Optional<Stock> existing = stockRepository.findByName(teaName);

        if (existing.isPresent()) {
            Stock stock = existing.get();
            int newQty = Integer.parseInt(stock.getQuantity()) + Integer.parseInt(dto.getQuantity());
            stock.setQuantity(String.valueOf(newQty));
            stock.setExpiryDate(LocalDateTime.parse(dto.getExpiryDate()));
            stock.setNotes(dto.getNotes());
            stockRepository.save(stock);
            return teaName + " stock updated successfully";
        } else {
            Stock newStock = Stock.builder()
                    .stockId(newId)
                    .name(teaName)
                    .quantity(dto.getQuantity())
                    .expiryDate(LocalDateTime.parse(dto.getExpiryDate()))
                    .notes(dto.getNotes())
                    .build();
            stockRepository.save(newStock);
            return teaName + " stock added successfully";
        }
    }
}
