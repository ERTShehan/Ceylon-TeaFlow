package lk.ijse.edu.controller;

import lk.ijse.edu.dto.*;
import lk.ijse.edu.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register/customer")
    public ResponseEntity<APIResponse<String>> registerCustomer(@RequestBody RegisterCustomerDto registerCustomerDto) {
        return new ResponseEntity<>(new APIResponse<>(
                201, "Customer Registered Successfully", authService.registerCustomer(registerCustomerDto)
        ), HttpStatus.CREATED);
    }

    @PostMapping("/register/supplier")
    public ResponseEntity<APIResponse<String>> registerSupplier(@RequestBody RegisterSupplierDto dto) {
        return new ResponseEntity<>(new APIResponse<>(
                201, "Supplier Registered Successfully", authService.registerSupplier(dto)
        ), HttpStatus.CREATED);
    }

//    @PostMapping("/saveAdmin")
//    public ResponseEntity<APIResponse> saveAdmin(@RequestBody SaveAdminDto dto) {
//        return ResponseEntity.ok(new APIResponse(200, "OK", authService.saveAdmin(dto)));
//    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<String>> login(@RequestBody AuthDto dto) {
        return ResponseEntity.ok(new APIResponse<>(200, "OK", authService.login(dto)));
    }

    @GetMapping("/profile")
    public ResponseEntity<APIResponse<String>> profile() {
        return ResponseEntity.ok(new APIResponse<>(200, "OK", "This is a protected endpoint!"));
    }
}
