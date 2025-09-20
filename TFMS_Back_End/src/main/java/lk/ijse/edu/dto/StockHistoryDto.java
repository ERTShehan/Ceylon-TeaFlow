package lk.ijse.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockHistoryDto {
    private String date;
    private String teaType;
    private String expiryDate;
    private String quantity;
    private String note;
    private String type;
}
