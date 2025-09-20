package lk.ijse.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddNewStockDto {
    private String stockId;
    private String productName;
    private String quantity;
    private String expiryDate;
    private String notes;
}
