package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.MonthlyBillRecordDto;
import lk.ijse.edu.entity.TeaLeafPrice;
import lk.ijse.edu.entity.TeaLeafSupplier;
import lk.ijse.edu.repository.*;
import lk.ijse.edu.service.SupplierBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierBillServiceImpl implements SupplierBillService {
    private final TeaLeafSupplierRepository teaLeafSupplierRepository;
    private final TeaCountRepository teaCountRepository;
    private final AddTeaLeafPriceRepository addTeaLeafPriceRepository;
    private final AdvancePaymentRepository advancePaymentRepository;
    private final TeaPacketRequestRepository teaPacketRequestRepository;

    @Override
    public MonthlyBillRecordDto getMonthlyBill(String supplierId, int year, int month) {
        TeaLeafSupplier supplier = teaLeafSupplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        double totalKg = teaCountRepository.getTotalBySupplierAndMonth(supplierId, year, month);

        String effectiveMonth = String.format("%04d-%02d", year, month);
        Optional<TeaLeafPrice> teaLeafPrice = addTeaLeafPriceRepository.findByEffectiveMonth(effectiveMonth);
        double unitPrice = teaLeafPrice.map(TeaLeafPrice::getPricePerKg).orElse(0.0);

        double totalPrice = totalKg * unitPrice;

        double advancePayments = advancePaymentRepository.getTotalAdvanceBySupplierAndMonth(supplierId, year, month);
        double teaPacketCost = teaPacketRequestRepository.getTotalPacketCostBySupplierAndMonth(supplierId, year, month);

        double netPrice = totalPrice - advancePayments - teaPacketCost;

        return MonthlyBillRecordDto.builder()
                .supplierName(supplier.getFirstName() + " " + supplier.getLastName())
                .teaCardNumber(supplier.getTeaCardNumber())
                .year(year)
                .month(month)
                .totalWeight(totalKg)
                .unitPrice(unitPrice)
                .advancePayment(advancePayments)
                .teaPacketCost(teaPacketCost)
                .totalPrice(netPrice)
                .build();
    }
}
