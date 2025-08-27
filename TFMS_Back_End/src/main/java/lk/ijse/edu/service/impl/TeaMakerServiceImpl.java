package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.RegisterTeaMakerDto;
import lk.ijse.edu.entity.SystemUserRole;
import lk.ijse.edu.entity.TeaMaker;
import lk.ijse.edu.entity.User;
import lk.ijse.edu.repository.TeaMakerRepository;
import lk.ijse.edu.repository.UserRepository;
import lk.ijse.edu.service.TeaMakerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TeaMakerServiceImpl implements TeaMakerService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TeaMakerRepository teaMakerRepository;

    @Transactional
    @Override
    public String saveTeaMaker(RegisterTeaMakerDto registerTeaMakerDto) {
        if (userRepository.existsByUsername(registerTeaMakerDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (registerTeaMakerDto==null){
            throw new IllegalArgumentException("Register Tea Maker DTO cannot be null");
        }

        User user = User.builder()
                .username(registerTeaMakerDto.getUsername())
                .password(passwordEncoder.encode(registerTeaMakerDto.getPassword()))
                .createdAt(new Date())
                .role(SystemUserRole.TEAM_MAKER)
                .build();

        TeaMaker teaMaker = TeaMaker.builder()
                .fullName(registerTeaMakerDto.getFullName())
                .email(registerTeaMakerDto.getEmail())
                .phoneNumber(registerTeaMakerDto.getPhoneNumber())
                .user(user)
                .build();

        userRepository.save(user);
        teaMakerRepository.save(teaMaker);
        return "Tea Maker registration success";
    }
}
