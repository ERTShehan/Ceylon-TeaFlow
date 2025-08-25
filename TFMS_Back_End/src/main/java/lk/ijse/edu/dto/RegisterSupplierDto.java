package lk.ijse.edu.dto;

import lombok.Data;

@Data
public class RegisterSupplierDto {
    private String username;
    private String password;

    private String teaCardNumber;

    private String firstName;
    private String lastName;
    private String email;
    private String address;
}
