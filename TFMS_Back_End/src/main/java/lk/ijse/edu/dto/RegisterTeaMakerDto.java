package lk.ijse.edu.dto;

import lombok.Data;

@Data
public class RegisterTeaMakerDto {
    private String username;
    private String password;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;
}
