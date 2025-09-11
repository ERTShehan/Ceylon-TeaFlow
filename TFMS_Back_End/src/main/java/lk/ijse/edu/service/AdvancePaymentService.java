package lk.ijse.edu.service;

import lk.ijse.edu.dto.AdvancePaymentsDto;

import java.util.List;

public interface AdvancePaymentService {
    String saveAdvancePayment(AdvancePaymentsDto advancePaymentsDto, String username);
    List<AdvancePaymentsDto> getAdvancePaymentsForSupplier(String username);
}
