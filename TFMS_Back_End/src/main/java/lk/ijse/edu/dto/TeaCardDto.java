package lk.ijse.edu.dto;

import lombok.Data;

@Data
public class TeaCardDto {
    private String id;
    private String number;
    private String name;
    private boolean used = false;
    private String issuedAt;
}
