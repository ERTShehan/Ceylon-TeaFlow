package lk.ijse.edu.service;

import lk.ijse.edu.dto.*;
import lk.ijse.edu.entity.*;
import lk.ijse.edu.repository.NormalCustomerRepository;
import lk.ijse.edu.repository.TeaCardRepository;
import lk.ijse.edu.repository.TeaLeafSupplierRepository;
import lk.ijse.edu.repository.UserRepository;
import lk.ijse.edu.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {
    private final UserRepository userRepository;
    private final NormalCustomerRepository normalCustomerRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeaLeafSupplierRepository teaLeafSupplierRepository;
    private final TeaCardRepository teaCardRepository;
    private final JWTUtil jwt;

    @Transactional
    public String registerCustomer(RegisterCustomerDto registerCustomerDto) {
        if (userRepository.existsByUsername(registerCustomerDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (registerCustomerDto.getEmail() != null && normalCustomerRepository.existsByEmail(registerCustomerDto.getEmail())) {
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
        userRepository.save(user);
        return "Customer registration success";
    }

    public String saveAdmin(SaveAdminDto dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .createdAt(new Date())
                .role(SystemUserRole.ADMIN)
                .build();

        Admin admin = Admin.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .user(user)
                .build();

        user.setAdmin(admin);
        userRepository.save(user);
        return "Admin saved successfully";
    }

    @Transactional
    public String registerSupplier(RegisterSupplierDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (dto.getEmail() != null && teaLeafSupplierRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        TeaCard teaCard = teaCardRepository.findByNumber(dto.getTeaCardNumber())
                .orElseThrow(() -> new RuntimeException("Invalid tea card number. Please contact CEO."));
        if (teaCard.isUsed()) {
            throw new RuntimeException("Tea card number already used");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .createdAt(new Date())
                .role(SystemUserRole.TEA_LEAF_SUPPLIER)
                .build();

        TeaLeafSupplier supplier = TeaLeafSupplier.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .teaCardNumber(dto.getTeaCardNumber())
                .user(user)
                .build();

        teaCard.setUsed(true);
        user.setTeaLeafSupplier(supplier);
        userRepository.save(user);

        return "Supplier registration success";
    }

    public AuthResponseDto login(AuthDto dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String role = user.getRole().name();
        String name = user.getNormalCustomer() != null ?
                user.getNormalCustomer().getFirstName() + " " + user.getNormalCustomer().getLastName() :
                user.getTeaLeafSupplier() != null ?
                        user.getTeaLeafSupplier().getFirstName() + " " + user.getTeaLeafSupplier().getLastName() :
                        "User";
        String access = jwt.generateAccessToken(user.getUsername(), role);

        long exp = jwt.getExpiry(access).getTime();

        return new AuthResponseDto(access, role, exp, name);
    }
}
