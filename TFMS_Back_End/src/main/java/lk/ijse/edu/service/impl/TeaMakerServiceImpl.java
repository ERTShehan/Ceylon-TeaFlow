package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.TeaMakerDto;
import lk.ijse.edu.entity.SystemUserRole;
import lk.ijse.edu.entity.TeaMaker;
import lk.ijse.edu.entity.User;
import lk.ijse.edu.exception.ResourceNotFound;
import lk.ijse.edu.repository.TeaMakerRepository;
import lk.ijse.edu.repository.UserRepository;
import lk.ijse.edu.service.TeaMakerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeaMakerServiceImpl implements TeaMakerService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TeaMakerRepository teaMakerRepository;

    @Transactional
    @Override
    public String saveTeaMaker(TeaMakerDto teaMakerDto) {
        if (userRepository.existsByUsername(teaMakerDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (teaMakerDto ==null){
            throw new IllegalArgumentException("Register Tea Maker DTO cannot be null");
        }

        User user = User.builder()
                .username(teaMakerDto.getUsername())
                .password(passwordEncoder.encode(teaMakerDto.getPassword()))
                .createdAt(new Date())
                .role(SystemUserRole.TEAM_MAKER)
                .build();

        TeaMaker teaMaker = TeaMaker.builder()
                .fullName(teaMakerDto.getFullName())
                .email(teaMakerDto.getEmail())
                .phoneNumber(teaMakerDto.getPhoneNumber())
                .user(user)
                .build();

        userRepository.save(user);
        teaMakerRepository.save(teaMaker);
        return "Tea Maker registration success";
    }

    @Transactional
    @Override
    public String updateTeaMaker(TeaMakerDto teaMakerDto) {
        if (teaMakerDto ==null||teaMakerDto.getId()==null){
            throw new IllegalArgumentException("Register Tea Maker DTO cannot be null");
        }

        TeaMaker existingTeaMaker = teaMakerRepository.findById(Long.valueOf(teaMakerDto.getId()))
                .orElseThrow(() -> new ResourceNotFound("Tea Maker not found"));

        existingTeaMaker.setFullName(teaMakerDto.getFullName());
        existingTeaMaker.setEmail(teaMakerDto.getEmail());
        existingTeaMaker.setPhoneNumber(teaMakerDto.getPhoneNumber());

        User existingUser = existingTeaMaker.getUser();
        existingUser.setUsername(teaMakerDto.getUsername());
        if (teaMakerDto.getPassword() != null && !teaMakerDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(teaMakerDto.getPassword()));
        }

        userRepository.save(existingUser);
        teaMakerRepository.save(existingTeaMaker);
        return "Tea Maker update success";
    }

    @Transactional
    @Override
    public String deleteTeaMaker(String id) {
        TeaMaker teaMaker = teaMakerRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResourceNotFound("Tea Maker not found"));

        teaMakerRepository.delete(teaMaker);
        if (teaMaker.getUser() != null) {
            userRepository.delete(teaMaker.getUser());
        }

        return "Tea Maker deletion success";
    }

    @Override
    public List<TeaMakerDto> getAllTeaMakers() {
        return List.of();
    }


}
