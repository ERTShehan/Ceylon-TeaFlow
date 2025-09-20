package lk.ijse.edu.dto;

import lk.ijse.edu.entity.TeaProductName;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockResponseDto {
    private String stockId;
    private String productName;
    private String quantity;
    private String expiryDate;
    private String notes;
}
