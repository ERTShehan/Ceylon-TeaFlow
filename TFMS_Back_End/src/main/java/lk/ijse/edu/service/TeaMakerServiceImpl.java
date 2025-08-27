package lk.ijse.edu.service;

import lk.ijse.edu.dto.RegisterTeaMakerDto;
import lk.ijse.edu.entity.SystemUserRole;
import lk.ijse.edu.entity.TeaMaker;
import lk.ijse.edu.entity.User;
import lk.ijse.edu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TeaMakerServiceImpl {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public String saveTeaMaker(RegisterTeaMakerDto registerTeaMakerDto) {
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

        user.setTeaMaker(teaMaker);
        userRepository.save(user);
        return "Tea Maker registration success";
    }
}
