package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.AddNewStockDto;
import lk.ijse.edu.dto.StockHistoryDto;
import lk.ijse.edu.dto.StockReportDto;
import lk.ijse.edu.dto.StockResponseDto;
import lk.ijse.edu.entity.Stock;
import lk.ijse.edu.entity.TeaProduct;
import lk.ijse.edu.entity.TeaProductName;
import lk.ijse.edu.repository.StockRepository;
import lk.ijse.edu.repository.TeaProductRepository;
import lk.ijse.edu.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


    @Override
    public List<StockResponseDto> getGroupedStockLevels() {
        List<Object[]> rows = stockRepository.findGroupedStockLevels();
        List<StockResponseDto> list = new ArrayList<>();

        for (Object[] row : rows) {
            TeaProductName name = (TeaProductName) row[0];
            Long totalQty = (Long) row[1];

            list.add(StockResponseDto.builder()
                    .productName(name.name())
                    .quantity(totalQty.toString())
                    .build());
        }
        return list;
    }

    @Override
    public String addNewStock(AddNewStockDto dto) {
        String lastId = stockRepository.findLastStockId();
        String nextId = generateNextStockId(lastId);

        TeaProductName teaName = TeaProductName.valueOf(dto.getProductName());

        Stock stock = Stock.builder()
                .stockId(nextId)
                .name(teaName)
                .quantity(dto.getQuantity())
                .expiryDate(LocalDateTime.parse(dto.getExpiryDate()))
                .notes(dto.getNotes())
                .dateTime(LocalDateTime.now())
                .type("INCOMING")
                .build();

        stockRepository.save(stock);
        return "Stock saved for " + teaName;
    }

    @Override
    public Page<StockHistoryDto> getStockHistory(int page, int size, String filter) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Stock> stockPage;

        if ("ALL".equalsIgnoreCase(filter)) {
            stockPage = stockRepository.findAllByOrderByDateTimeDesc(pageable);
        } else {
            stockPage = stockRepository.findByTypeOrderByDateTimeDesc(filter.toUpperCase(), pageable);
        }

        return stockPage.map(s -> new StockHistoryDto(
                s.getDateTime().toLocalDate().toString(),
                s.getName().name(),
                s.getExpiryDate() != null ? s.getExpiryDate().toLocalDate().toString() : "-",
                s.getQuantity(),
                s.getNotes(),
                s.getType()
        ));
    }

    @Override
    public Long getTotalStockQuantity() {
        Long total = stockRepository.findTotalStockQuantity();
        return total != null ? total : 0L;
    }

    @Override
    public List<StockReportDto> getStockSummary() {
        List<Stock> stocks = stockRepository.findAll();

        Map<TeaProductName, Integer> summary = stocks.stream()
                .collect(Collectors.groupingBy(
                        Stock::getName,
                        Collectors.summingInt(s -> Integer.parseInt(s.getQuantity()))
                ));

        return summary.entrySet().stream()
                .map(e -> new StockReportDto(e.getKey(), String.valueOf(e.getValue())))
                .collect(Collectors.toList());
    }
}
