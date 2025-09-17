package lk.ijse.edu.service;

import lk.ijse.edu.dto.MonthlySupplySummaryDto;

public interface SupplierTotalPriceService {
    MonthlySupplySummaryDto getSupplierMonthlyTotalPrice(String supplierId, int year, int month);
}
