package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.AuthDto;
import lk.ijse.edu.dto.RegisterCustomerDto;
import lk.ijse.edu.dto.RegisterSupplierDto;
import lk.ijse.edu.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register/customer")
    public ResponseEntity<APIResponse> registerCustomer(@RequestBody RegisterCustomerDto registerCustomerDto) {
        return ResponseEntity.ok(new APIResponse(
                200,
                "OK",
                authService.registerCustomer(registerCustomerDto)
        ));
    }

    @PostMapping("/register/supplier")
    public ResponseEntity<APIResponse> registerSupplier(@RequestBody RegisterSupplierDto dto) {
        return ResponseEntity.ok(new APIResponse(200, "OK", authService.registerSupplier(dto)));
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse> login(@RequestBody AuthDto dto) {
        return ResponseEntity.ok(new APIResponse(200, "OK", authService.login(dto)));
    }

    @GetMapping("/profile")
    public ResponseEntity<APIResponse> profile() {
        return ResponseEntity.ok(new APIResponse(200, "OK", "This is a protected endpoint!"));
    }
}
