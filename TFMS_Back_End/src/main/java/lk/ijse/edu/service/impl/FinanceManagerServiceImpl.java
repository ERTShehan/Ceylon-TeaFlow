package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.FinanceManagerDto;
import lk.ijse.edu.entity.FinanceManager;
import lk.ijse.edu.entity.SystemUserRole;
import lk.ijse.edu.entity.User;
import lk.ijse.edu.exception.ResourceNotFound;
import lk.ijse.edu.repository.FinanceManagerRepository;
import lk.ijse.edu.repository.UserRepository;
import lk.ijse.edu.service.FinanceManagerService;
import lk.ijse.edu.util.IdGenerate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceManagerServiceImpl implements FinanceManagerService {
    private final UserRepository userRepository;
    private final FinanceManagerRepository financeManagerRepository;
    private final IdGenerate idGenerate;
    private final PasswordEncoder passwordEncoder;

    private String generateNextFinanceManagerId(String lastId) {
        if (lastId == null) return "FM-000-001";

        String[] parts = lastId.split("-");
        int major = Integer.parseInt(parts[1]);
        int minor = Integer.parseInt(parts[2]);

        minor++;

        if (minor > 999) {
            minor = 1;
            major++;
        }

        if (major > 999) {
            throw new IllegalStateException("ID count has ended. Please contact the developer.");
        }

        return String.format("FM-%03d-%03d", major, minor);
    }

    @Override
    public String saveFinanceManager(FinanceManagerDto financeManagerDto) {
        if (userRepository.existsByUsername(financeManagerDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (financeManagerDto ==null){
            throw new IllegalArgumentException("Register Finance Manager DTO cannot be null");
        }

        String lastId = financeManagerRepository.findLastFinanceManagerId();
        String newFinanceManagerId = generateNextFinanceManagerId(lastId);

        String lastUserId = userRepository.findLastUserId();
        String newUserId = idGenerate.generateNextUserId(lastUserId);

        User user = User.builder()
                .id(newUserId)
                .username(financeManagerDto.getUsername())
                .password(passwordEncoder.encode(financeManagerDto.getPassword()))
                .createdAt(new Date())
                .role(SystemUserRole.FINANCE_MANAGER)
                .build();

        FinanceManager financeManager = FinanceManager.builder()
                .financeManagerId(newFinanceManagerId)
                .fullName(financeManagerDto.getFullName())
                .email(financeManagerDto.getEmail())
                .phoneNumber(financeManagerDto.getPhoneNumber())
                .basicSalary(financeManagerDto.getBasicSalary())
                .status(financeManagerDto.getStatus())
                .user(user)
                .build();

        userRepository.save(user);
        financeManagerRepository.save(financeManager);
        return "Finance Manager registered successfully";
    }

    @Transactional
    @Override
    public String updateFinanceManager(FinanceManagerDto financeManagerDto) {
        if (financeManagerDto == null|| financeManagerDto.getId() == null) {
            throw new IllegalArgumentException("Update Finance Manager DTO cannot be null");
        }

        FinanceManager existingFinanceManager = financeManagerRepository.findById(financeManagerDto.getId())
                .orElseThrow(() -> new RuntimeException("Finance Manager not found"));

        existingFinanceManager.setFullName(financeManagerDto.getFullName());
        existingFinanceManager.setEmail(financeManagerDto.getEmail());
        existingFinanceManager.setPhoneNumber(financeManagerDto.getPhoneNumber());
        existingFinanceManager.setBasicSalary(financeManagerDto.getBasicSalary());
        existingFinanceManager.setStatus(financeManagerDto.getStatus());

        User existingUser = existingFinanceManager.getUser();
        existingUser.setUsername(financeManagerDto.getUsername());
        if (financeManagerDto.getPassword() != null && !financeManagerDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(financeManagerDto.getPassword()));
        }

        userRepository.save(existingUser);
        financeManagerRepository.save(existingFinanceManager);
        return "Finance Manager updated successfully";
    }

    @Transactional
    @Override
    public void deleteFinanceManager(String id) {
        FinanceManager financeManager = financeManagerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Finance Manager not found"));

        User linkedUser = financeManager.getUser();
        if (linkedUser != null) {
            userRepository.delete(linkedUser);
        }
        financeManagerRepository.delete(financeManager);
    }

    @Override
    public List<FinanceManagerDto> getAllFinanceManagers() {
        List<FinanceManager> allFinanceManagers = financeManagerRepository.findAll();
        if (allFinanceManagers.isEmpty()) {
            throw new ResourceNotFound("No Finance Managers found");
        }

        return allFinanceManagers.stream().map(fm -> {
            FinanceManagerDto dto = new FinanceManagerDto();
            dto.setId(fm.getFinanceManagerId());
            dto.setFullName(fm.getFullName());
            dto.setEmail(fm.getEmail());
            dto.setPhoneNumber(fm.getPhoneNumber());
            dto.setStatus(fm.getStatus());
            dto.setUsername(fm.getUser().getUsername());
            dto.setRole(fm.getUser().getRole().name());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<FinanceManagerDto> searchFinanceManager(String keyword) {
        if (keyword==null){
            throw new IllegalArgumentException("Keyword cannot be null");
        }

        List<FinanceManager> allFinanceManagers = financeManagerRepository.findFinanceManagerByFullNameContainingIgnoreCase(keyword);

        if (allFinanceManagers.isEmpty()) {
            throw new ResourceNotFound("No Finance Managers found with the given keyword");
        }

        return allFinanceManagers.stream().map(fm -> {
            FinanceManagerDto dto = new FinanceManagerDto();
            dto.setId(fm.getFinanceManagerId());
            dto.setFullName(fm.getFullName());
            dto.setEmail(fm.getEmail());
            dto.setPhoneNumber(fm.getPhoneNumber());
            dto.setStatus(fm.getStatus());
            dto.setUsername(fm.getUser().getUsername());
            dto.setRole(fm.getUser().getRole().name());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void changeFinanceManagerStatus(String id) {
        if (financeManagerRepository.findById(id).isEmpty()) {
            throw new ResourceNotFound("Finance Manager not found");
        }

        if (id == null) {
            throw new IllegalArgumentException("Finance Manager ID cannot be null");
        }

        financeManagerRepository.updateFinanceManagerStatus(id);
    }
}
