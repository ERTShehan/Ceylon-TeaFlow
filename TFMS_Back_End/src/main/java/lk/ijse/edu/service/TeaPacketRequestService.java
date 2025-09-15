package lk.ijse.edu.service;

import lk.ijse.edu.dto.ApplyPacketResponseDto;
import lk.ijse.edu.entity.TeaPacketRequest;

public interface TeaPacketRequestService {
    ApplyPacketResponseDto applyPacket(String productId, String supplierUsername);
}
