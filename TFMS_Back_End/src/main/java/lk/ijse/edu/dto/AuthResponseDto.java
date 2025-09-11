package lk.ijse.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    private String role;
    private long accessExpiresAtEpochMs;
//    private String name;
}
