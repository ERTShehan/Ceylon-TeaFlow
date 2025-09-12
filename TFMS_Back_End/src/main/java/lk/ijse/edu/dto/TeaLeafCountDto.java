package lk.ijse.edu.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
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
    private String note;
}
