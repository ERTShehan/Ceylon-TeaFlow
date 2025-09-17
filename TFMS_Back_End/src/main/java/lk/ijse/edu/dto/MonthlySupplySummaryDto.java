package lk.ijse.edu.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MonthlySupplySummaryDto {
    private double totalKg;
    private double totalPrice;
}
