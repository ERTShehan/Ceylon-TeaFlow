package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.AdvancePaymentsDto;
import lk.ijse.edu.entity.AdvancePayment;
import lk.ijse.edu.entity.RequestStatus;
import lk.ijse.edu.entity.TeaLeafSupplier;
import lk.ijse.edu.entity.User;
import lk.ijse.edu.repository.AdvancePaymentRepository;
import lk.ijse.edu.repository.TeaLeafSupplierRepository;
import lk.ijse.edu.repository.UserRepository;
import lk.ijse.edu.service.AdvancePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvancePaymentServiceImpl implements AdvancePaymentService {
    private final AdvancePaymentRepository advancePaymentRepository;
    private final TeaLeafSupplierRepository teaLeafSupplierRepository;
    private final UserRepository userRepository;

    private String generateNextAdvancePaymentId(String lastId) {
        if (lastId == null) return "AD-000-001";

        String[] parts = lastId.split("-");
        int major = Integer.parseInt(parts[1]);
        int minor = Integer.parseInt(parts[2]);

        minor++;

        if (minor > 999) {
            minor = 1;
            major++;
        }

        if (major > 999) {
            throw new IllegalStateException("ID count has ended. Please contact the developer.");
        }

        return String.format("AD-%03d-%03d", major, minor);
    }

    @Override
    public String saveAdvancePayment(AdvancePaymentsDto advancePaymentsDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TeaLeafSupplier supplier = user.getTeaLeafSupplier();
        if (supplier == null) {
            throw new RuntimeException("Logged in user is not a supplier");
        }

        String lastId = advancePaymentRepository.findLastAdvancePaymentId();
        String newAdvancePaymentId = generateNextAdvancePaymentId(lastId);

//        TeaLeafSupplier supplier = teaLeafSupplierRepository.findById(advancePaymentsDto.getSupplierId())
//                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        AdvancePayment advancePayment = AdvancePayment.builder()
                .id(newAdvancePaymentId)
                .supplier(supplier)
                .amount(advancePaymentsDto.getAmount())
                .paymentDate(new Date())
                .reason(advancePaymentsDto.getReason())
                .status(RequestStatus.PENDING)
                .build();

        advancePaymentRepository.save(advancePayment);
        return "Advance Payment Application Submitted Successfully";
    }

    @Override
    public List<AdvancePaymentsDto> getAdvancePaymentsForSupplier(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TeaLeafSupplier supplier = user.getTeaLeafSupplier();
        if (supplier == null) {
            throw new RuntimeException("Logged in user is not a supplier");
        }

        List<AdvancePayment> payments = advancePaymentRepository.findBySupplierOrderByPaymentDateDesc(supplier);



        return payments.stream().map(ap -> new AdvancePaymentsDto(
                ap.getId(),
                ap.getAmount(),
                ap.getPaymentDate().toString(),
                ap.getReason(),
                supplier.getSupplierId(),
                ap.getStatus().name()
        )).toList();
    }
}
