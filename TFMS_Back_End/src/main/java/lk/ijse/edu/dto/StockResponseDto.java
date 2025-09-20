package lk.ijse.edu.dto;

import lk.ijse.edu.entity.TeaProductName;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockResponseDto {
    private String productName;
    private String quantity;
}
