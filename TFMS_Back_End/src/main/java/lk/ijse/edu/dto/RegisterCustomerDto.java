package lk.ijse.edu.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterCustomerDto {
    private String username;
    private String password;

    private String firstName;
    private String lastName;
    private String email;
    private String address;
}
