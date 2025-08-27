package lk.ijse.edu.service;

import lk.ijse.edu.dto.AuthDto;
import lk.ijse.edu.dto.AuthResponseDto;
import lk.ijse.edu.dto.RegisterCustomerDto;
import lk.ijse.edu.dto.RegisterSupplierDto;

public interface AuthService {
    String registerCustomer(RegisterCustomerDto registerCustomerDto);
    String registerSupplier(RegisterSupplierDto registerSupplierDto);
    AuthResponseDto login(AuthDto dto);
}
