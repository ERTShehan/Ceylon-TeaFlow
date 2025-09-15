package lk.ijse.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyPacketResponseDto {
    private String productId;
    private String supplierId;
    private String message;
}
