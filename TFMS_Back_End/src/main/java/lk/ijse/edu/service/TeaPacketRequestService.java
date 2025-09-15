package lk.ijse.edu.service;

import lk.ijse.edu.dto.ApplyPacketResponseDto;
import lk.ijse.edu.dto.TeaPacketRequestDto;

import java.util.List;

public interface TeaPacketRequestService {
    ApplyPacketResponseDto applyPacket(String productId, String supplierUsername);
    long getTotalTeaPacketRequestsThisMonth(String supplierId);
    List<TeaPacketRequestDto> getAllRequestsBySupplier(String supplierId);
}
