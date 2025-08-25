package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.RegisterCustomerDto;
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
}
