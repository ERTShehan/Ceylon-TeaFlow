package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.TeaMakerDto;
import lk.ijse.edu.entity.SystemUserRole;
import lk.ijse.edu.entity.TeaMaker;
import lk.ijse.edu.entity.User;
import lk.ijse.edu.exception.ResourceNotFound;
import lk.ijse.edu.repository.TeaMakerRepository;
import lk.ijse.edu.repository.UserRepository;
import lk.ijse.edu.service.TeaMakerService;
import lk.ijse.edu.util.IdGenerate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeaMakerServiceImpl implements TeaMakerService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TeaMakerRepository teaMakerRepository;
    private final IdGenerate idGenerate;

    private String generateNextSupplierId(String lastId) {
        if (lastId == null) return "TM-000-001";

        String[] parts = lastId.split("-");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);

        minor++;

        if (minor > 999) {
            minor = 1;
            major++;
        }

        if (major > 999) {
            throw new IllegalStateException("Supplier ID count has ended. Please contact the developer.");
        }

        return String.format("TM-%03d-%03d", major, minor);
    }

    @Transactional
    @Override
    public String saveTeaMaker(TeaMakerDto teaMakerDto) {
        if (userRepository.existsByUsername(teaMakerDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (teaMakerDto ==null){
            throw new IllegalArgumentException("Register Tea Maker DTO cannot be null");
        }

        String lastId = teaMakerRepository.findLastTeaMakerId();
        String newTeaMakerId = generateNextSupplierId(lastId);

        String lastUserId = userRepository.findLastUserId();
        String newUserId = idGenerate.generateNextUserId(lastUserId);

        User user = User.builder()
                .id(newUserId)
                .username(teaMakerDto.getUsername())
                .password(passwordEncoder.encode(teaMakerDto.getPassword()))
                .createdAt(new Date())
                .role(SystemUserRole.TEAM_MAKER)
                .build();

        TeaMaker teaMaker = TeaMaker.builder()
                .teaMakerId(newTeaMakerId)
                .fullName(teaMakerDto.getFullName())
                .email(teaMakerDto.getEmail())
                .phoneNumber(teaMakerDto.getPhoneNumber())
                .basicSalary(teaMakerDto.getBasicSalary())
                .status(teaMakerDto.getStatus())
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

        TeaMaker existingTeaMaker = teaMakerRepository.findById(String.valueOf(teaMakerDto))
                .orElseThrow(() -> new ResourceNotFound("Tea Maker not found"));

        existingTeaMaker.setFullName(teaMakerDto.getFullName());
        existingTeaMaker.setEmail(teaMakerDto.getEmail());
        existingTeaMaker.setPhoneNumber(teaMakerDto.getPhoneNumber());
        existingTeaMaker.setBasicSalary(teaMakerDto.getBasicSalary());
        existingTeaMaker.setStatus(teaMakerDto.getStatus());

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
        TeaMaker teaMaker = teaMakerRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new ResourceNotFound("Tea Maker not found"));

        User linkedUser = teaMaker.getUser();
        teaMaker.setUser(null);
        teaMakerRepository.save(teaMaker);

        teaMakerRepository.delete(teaMaker);
        if (linkedUser != null) {
            userRepository.delete(linkedUser);
        }

        return "Tea Maker deletion success";
    }

    @Override
    public List<TeaMakerDto> getAllTeaMakers() {
        List<TeaMaker> allTeaMakers = teaMakerRepository.findAll();
        if (allTeaMakers.isEmpty()) {
            throw new ResourceNotFound("No tea Maker Found");
        }

        return allTeaMakers.stream().map(tm -> {
            TeaMakerDto dto = new TeaMakerDto();
            dto.setId(String.valueOf(tm.getTeaMakerId()));
            dto.setFullName(tm.getFullName());
            dto.setEmail(tm.getEmail());
            dto.setPhoneNumber(tm.getPhoneNumber());
            if (tm.getUser() != null) {
                dto.setUsername(tm.getUser().getUsername());
                dto.setRole(tm.getUser().getRole().name());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TeaMakerDto> searchTeaMaker(String keyword) {
        if (keyword==null){
            throw new IllegalArgumentException("Keyword cannot be null");
        }

        List<TeaMaker> allTeaMakers = teaMakerRepository.findTeaMakerByFullNameContainingIgnoreCase(keyword);

        if (allTeaMakers.isEmpty()){
            throw new ResourceNotFound("No Tea Maker Found");
        }

        return allTeaMakers.stream().map(tm -> {
            TeaMakerDto dto = new TeaMakerDto();
            dto.setId(String.valueOf(tm.getTeaMakerId()));
            dto.setFullName(tm.getFullName());
            dto.setEmail(tm.getEmail());
            dto.setPhoneNumber(tm.getPhoneNumber());
            dto.setUsername(tm.getUser().getUsername());
            dto.setRole(tm.getUser().getRole().name());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void changeTeaMakerStatus(String id) {
        if (id==null){
            throw new IllegalArgumentException("Tea Maker Id cannot be null");
        }

        teaMakerRepository.updateTeaMakerStatus(id);
    }
}