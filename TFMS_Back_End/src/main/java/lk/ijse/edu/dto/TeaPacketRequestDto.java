package lk.ijse.edu.dto;

import lk.ijse.edu.entity.OrderStatus;
import lk.ijse.edu.entity.RequestStatus;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TeaPacketRequestDto {
    private String requestId;
    private String productName;
    private double price;
    private String weight;
    private RequestStatus status;
    private Date requestDate;
}
