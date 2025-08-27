package lk.ijse.edu.controller;

import lk.ijse.edu.dto.*;
import lk.ijse.edu.service.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/register/customer")
    public ResponseEntity<APIResponse> registerCustomer(@RequestBody RegisterCustomerDto registerCustomerDto) {
        return new ResponseEntity<>(new APIResponse(
                201, "Customer Registered Successfully", authServiceImpl.registerCustomer(registerCustomerDto)
        ), HttpStatus.CREATED);
    }

    @PostMapping("/register/supplier")
    public ResponseEntity<APIResponse> registerSupplier(@RequestBody RegisterSupplierDto dto) {
        return new ResponseEntity<>(new APIResponse(
                201, "Supplier Registered Successfully", authServiceImpl.registerSupplier(dto)
        ), HttpStatus.CREATED);
    }

//    @PostMapping("/saveAdmin")
//    public ResponseEntity<APIResponse> saveAdmin(@RequestBody SaveAdminDto dto) {
//        return ResponseEntity.ok(new APIResponse(200, "OK", authService.saveAdmin(dto)));
//    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse> login(@RequestBody AuthDto dto) {
        return ResponseEntity.ok(new APIResponse(200, "OK", authServiceImpl.login(dto)));
    }

    @GetMapping("/profile")
    public ResponseEntity<APIResponse> profile() {
        return ResponseEntity.ok(new APIResponse(200, "OK", "This is a protected endpoint!"));
    }
}
