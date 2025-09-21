package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.MonthlySupplySummaryDto;
import lk.ijse.edu.entity.TeaLeafPrice;
import lk.ijse.edu.repository.AddTeaLeafPriceRepository;
import lk.ijse.edu.repository.TeaCountRepository;
import lk.ijse.edu.service.SupplierTotalPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierTotalPriceServiceImpl implements SupplierTotalPriceService {
    private final TeaCountRepository teaCountRepository;
    private final AddTeaLeafPriceRepository addTeaLeafPriceRepository;

    @Override
    public MonthlySupplySummaryDto getSupplierMonthlyTotalPrice(String supplierId, int year, int month) {
        double totalKg = teaCountRepository.getTotalBySupplierAndMonth(supplierId, year, month);

        String effectiveMonth = String.format("%04d-%02d", year, month);
        Optional<TeaLeafPrice> teaLeafPrice = addTeaLeafPriceRepository.findByEffectiveMonth(effectiveMonth);

        double pricePerKg = teaLeafPrice.map(TeaLeafPrice::getPricePerKg).orElse(0.0);
        double totalPrice = totalKg * pricePerKg;

        return new MonthlySupplySummaryDto(totalKg, totalPrice);
    }
}
