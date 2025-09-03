package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.SalesManagerDto;
import lk.ijse.edu.entity.SalesManager;
import lk.ijse.edu.entity.SystemUserRole;
import lk.ijse.edu.entity.User;
import lk.ijse.edu.exception.ResourceNotFound;
import lk.ijse.edu.repository.SalesManagerRepository;
import lk.ijse.edu.repository.UserRepository;
import lk.ijse.edu.service.SalesManagerService;
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
public class SalesManagerServiceImpl implements SalesManagerService {
    private final UserRepository userRepository;
    private final SalesManagerRepository salesManagerRepository;
    private final IdGenerate idGenerate;
    private final PasswordEncoder passwordEncoder;

    private String generateNextSalesManagerId(String lastId) {
        if (lastId == null) return "SAM-000-001";

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

        return String.format("SAM-%03d-%03d", major, minor);
    }

    @Transactional
    @Override
    public String saveSalesManager(SalesManagerDto salesManagerDto) {
        if (userRepository.existsByUsername(salesManagerDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (salesManagerDto ==null){
            throw new IllegalArgumentException("Register Sales Manager DTO cannot be null");
        }

        String lastId = salesManagerRepository.findLastSalesManagerId();
        String newSalesManagerId = generateNextSalesManagerId(lastId);

        String lastUserId = userRepository.findLastUserId();
        String newUserId = idGenerate.generateNextUserId(lastUserId);

        User user = User.builder()
                .id(newUserId)
                .username(salesManagerDto.getUsername())
                .password(passwordEncoder.encode(salesManagerDto.getPassword()))
                .createdAt(new Date())
                .role(SystemUserRole.SALES_MANAGER)
                .build();

        SalesManager salesManager = SalesManager.builder()
                .SalesManagerId(newSalesManagerId)
                .fullName(salesManagerDto.getFullName())
                .email(salesManagerDto.getEmail())
                .phoneNumber(salesManagerDto.getPhoneNumber())
                .basicSalary(salesManagerDto.getBasicSalary())
                .status("Active")
                .user(user)
                .build();

        userRepository.save(user);
        salesManagerRepository.save(salesManager);
        return "Sales Manager registered successfully";
    }

    @Transactional
    @Override
    public String updateSalesManager(SalesManagerDto salesManagerDto) {
        if (salesManagerDto == null|| salesManagerDto.getId() == null) {
            throw new IllegalArgumentException("Update Sales Manager DTO cannot be null");
        }

        SalesManager existingSalesManager = salesManagerRepository.findById(salesManagerDto.getId())
                .orElseThrow(() -> new RuntimeException("Sales Manager not found"));

        existingSalesManager.setFullName(salesManagerDto.getFullName());
        existingSalesManager.setEmail(salesManagerDto.getEmail());
        existingSalesManager.setPhoneNumber(salesManagerDto.getPhoneNumber());
        existingSalesManager.setBasicSalary(salesManagerDto.getBasicSalary());
        existingSalesManager.setStatus(salesManagerDto.getStatus());

        User existingUser = existingSalesManager.getUser();
        existingUser.setUsername(salesManagerDto.getUsername());
        if (salesManagerDto.getPassword() != null && !salesManagerDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(salesManagerDto.getPassword()));
        }

        userRepository.save(existingUser);
        salesManagerRepository.save(existingSalesManager);
        return "Sales Manager updated successfully";
    }

    @Transactional
    @Override
    public void deleteSalesManager(String id) {
        SalesManager salesManager = salesManagerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Sales Manager not found"));

        User linkedUser = salesManager.getUser();
        if (linkedUser != null) {
            userRepository.delete(linkedUser);
        }
        salesManagerRepository.delete(salesManager);
    }

    @Override
    public List<SalesManagerDto> getAllSalesManagers() {
        List<SalesManager> allSalesManagers = salesManagerRepository.findAll();
        if (allSalesManagers.isEmpty()) {
            throw new ResourceNotFound("No Sales Managers found");
        }

        return allSalesManagers.stream().map(sm -> {
            SalesManagerDto dto = new SalesManagerDto();
            dto.setId(sm.getSalesManagerId());
            dto.setFullName(sm.getFullName());
            dto.setEmail(sm.getEmail());
            dto.setPhoneNumber(sm.getPhoneNumber());
            dto.setStatus(sm.getStatus());
            dto.setUsername(sm.getUser().getUsername());
            dto.setRole(sm.getUser().getRole().name());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SalesManagerDto> searchSalesManager(String keyword) {
        if (keyword==null){
            throw new IllegalArgumentException("Keyword cannot be null");
        }

        List<SalesManager> allSalesManagers = salesManagerRepository.findSalesManagerByFullNameContainingIgnoreCase(keyword);

        if (allSalesManagers.isEmpty()) {
            throw new ResourceNotFound("No Sales Managers found with the given keyword");
        }

        return allSalesManagers.stream().map(sm -> {
            SalesManagerDto dto = new SalesManagerDto();
            dto.setId(sm.getSalesManagerId());
            dto.setFullName(sm.getFullName());
            dto.setEmail(sm.getEmail());
            dto.setPhoneNumber(sm.getPhoneNumber());
            dto.setStatus(sm.getStatus());
            dto.setUsername(sm.getUser().getUsername());
            dto.setRole(sm.getUser().getRole().name());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void changeSalesManagerStatus(String id) {
        if (!salesManagerRepository.existsById(id)) {
            throw new ResourceNotFound("Sales Manager not found");
        }

        if (id == null) {
            throw new IllegalArgumentException("Finance Manager ID cannot be null");
        }

        salesManagerRepository.updateSalesManagerStatus(id);
    }
}
