package lk.ijse.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTeaLeafPriceDto {
    private String id;
    private YearMonth yearMonth;
    private double pricePerKg;
}
