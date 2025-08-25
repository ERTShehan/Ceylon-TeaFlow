package lk.ijse.edu.service;

import lk.ijse.edu.dto.RegisterCustomerDto;
import lk.ijse.edu.entity.NormalCustomer;
import lk.ijse.edu.entity.SystemUserRole;
import lk.ijse.edu.entity.User;
import lk.ijse.edu.repository.NormalCustomerRepo;
import lk.ijse.edu.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepo userRepo;
    private final NormalCustomerRepo normalCustomerRepo;
    private final PasswordEncoder passwordEncoder;

    public String registerCustomer(RegisterCustomerDto registerCustomerDto) {
        if (userRepo.existsByUsername(registerCustomerDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (registerCustomerDto.getEmail() != null && normalCustomerRepo.existsByEmail(registerCustomerDto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .username(registerCustomerDto.getUsername())
                .password(passwordEncoder.encode(registerCustomerDto.getPassword()))
                .createdAt(new Date())
                .role(SystemUserRole.CUSTOMER)
                .build();

        NormalCustomer profile = NormalCustomer.builder()
                .firstName(registerCustomerDto.getFirstName())
                .lastName(registerCustomerDto.getLastName())
                .email(registerCustomerDto.getEmail())
                .address(registerCustomerDto.getAddress())
                .user(user)
                .build();

        user.setNormalCustomer(profile);
        userRepo.save(user);
        return "Customer registration success";
    }
}
