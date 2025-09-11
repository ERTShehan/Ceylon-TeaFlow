package lk.ijse.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeaLeafCountDto {
    private String id;
    private String teaCardNumber;
    private String supplierName;
    private String grossWeight;
    private String sackWeight;
    private String moistureWeight;
    private String netWeight;
    private String date;
    private String time;
    private String quality;
    private String supplierId;
    private String note;
}
