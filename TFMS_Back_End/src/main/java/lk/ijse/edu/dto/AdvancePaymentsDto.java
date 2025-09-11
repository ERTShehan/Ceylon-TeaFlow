package lk.ijse.edu.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdvancePaymentsDto {
    private String id;
    private String amount;
    private String date;
    private String reason;
    private String supplierId;
    private String status;
}
