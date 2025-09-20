package lk.ijse.edu.dto;
import lk.ijse.edu.entity.TeaProductName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockReportDto {
    private TeaProductName teaName;
    private String totalQuantity;
}
