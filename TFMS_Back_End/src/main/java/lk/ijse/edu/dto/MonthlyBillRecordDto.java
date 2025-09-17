package lk.ijse.edu.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyBillRecordDto {
    private String supplierName;
    private String teaCardNumber;
    private int year;
    private int month;
    private double totalWeight;
    private double unitPrice;
    private double advancePayment;
    private double teaPacketCost;
    private double totalPrice;
}
