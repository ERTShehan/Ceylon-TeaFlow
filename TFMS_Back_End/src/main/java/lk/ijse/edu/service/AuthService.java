package lk.ijse.edu.service;

import lk.ijse.edu.dto.*;

public interface AuthService {
    String registerCustomer(RegisterCustomerDto registerCustomerDto);
    String registerSupplier(RegisterSupplierDto registerSupplierDto);
    String saveAdmin(SaveAdminDto dto);
    AuthResponseDto login(AuthDto dto);
}
