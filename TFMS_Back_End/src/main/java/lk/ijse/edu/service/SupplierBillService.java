package lk.ijse.edu.service;

import lk.ijse.edu.dto.MonthlyBillRecordDto;

public interface SupplierBillService {
    MonthlyBillRecordDto getMonthlyBill(String supplierId, int year, int month);
}
