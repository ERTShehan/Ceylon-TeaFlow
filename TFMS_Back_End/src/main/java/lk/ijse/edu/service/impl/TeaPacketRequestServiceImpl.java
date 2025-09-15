package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.ApplyPacketResponseDto;
import lk.ijse.edu.dto.TeaPacketRequestDto;
import lk.ijse.edu.entity.*;
import lk.ijse.edu.repository.TeaLeafSupplierRepository;
import lk.ijse.edu.repository.TeaPacketRequestRepository;
import lk.ijse.edu.repository.TeaProductRepository;
import lk.ijse.edu.service.TeaPacketRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeaPacketRequestServiceImpl implements TeaPacketRequestService {
    private final TeaLeafSupplierRepository teaLeafSupplierRepository;
    private final TeaProductRepository teaProductRepository;
    private final TeaPacketRequestRepository teaPacketRequestRepository;

    private String generateNextTeaPacketRequestId(String lastId) {
        if (lastId == null) return "TQ-000-001";

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

        return String.format("TQ-%03d-%03d", major, minor);
    }


    @Transactional
    @Override
    public ApplyPacketResponseDto applyPacket(String productId, String supplierUsername) {
        String lastId = teaPacketRequestRepository.findLastTeaPacketRequestId();
        String nextId = generateNextTeaPacketRequestId(lastId);

        TeaLeafSupplier supplier = teaLeafSupplierRepository.findByUserUsername(supplierUsername)
                .orElseThrow(() -> new RuntimeException("Supplier not found for user: " + supplierUsername));

        TeaProduct product = teaProductRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        TeaPacketRequest request = TeaPacketRequest.builder()
                .requestId(nextId)
                .supplier(supplier)
                .product(product)
                .requestDate(new Date())
                .status(OrderStatus.PENDING)
                .build();

        teaPacketRequestRepository.save(request);

        return new ApplyPacketResponseDto(
                productId,
                supplier.getSupplierId(),
                "Packet request submitted successfully!"
        );
    }

    @Override
    public long getTotalTeaPacketRequestsThisMonth(String supplierId) {
        return teaPacketRequestRepository.countRequestsForSupplierThisMonth(supplierId);
    }

    @Override
    public List<TeaPacketRequestDto> getAllRequestsBySupplier(String supplierId) {
        List<TeaPacketRequest> requests = teaPacketRequestRepository.findBySupplier_SupplierId(supplierId);

        return requests.stream().map(r ->
                TeaPacketRequestDto.builder()
                        .requestId(r.getRequestId())
                        .productName(r.getProduct().getName().name())
                        .price(r.getProduct().getPrice())
                        .weight(r.getProduct().getQuantity())
                        .status(r.getStatus())
                        .requestDate(r.getRequestDate())
                        .build()
        ).collect(Collectors.toList());
    }
}
