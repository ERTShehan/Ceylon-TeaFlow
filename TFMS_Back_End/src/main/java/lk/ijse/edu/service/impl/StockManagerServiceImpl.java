package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.StockManagerDto;
import lk.ijse.edu.entity.StockManager;
import lk.ijse.edu.entity.SystemUserRole;
import lk.ijse.edu.entity.User;
import lk.ijse.edu.exception.ResourceNotFound;
import lk.ijse.edu.repository.StockManagerRepository;
import lk.ijse.edu.repository.UserRepository;
import lk.ijse.edu.service.StockManagerService;
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
public class StockManagerServiceImpl implements StockManagerService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final IdGenerate idGenerate;
    private final StockManagerRepository stockManagerRepository;

    private String generateNextStockManagerId(String lastId) {
        if (lastId == null) return "SM-000-001";

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

        return String.format("SM-%03d-%03d", major, minor);
    }

    @Transactional
    @Override
    public String saveStockManager(StockManagerDto stockManagerDto) {
        if (userRepository.existsByUsername(stockManagerDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

//        if (stockManagerDto ==null){
//            throw new IllegalArgumentException("Register Stock Manager DTO cannot be null");
//        }

        String lastId = stockManagerRepository.findLastStockManagerId();
        String newStockManagerId = generateNextStockManagerId(lastId);

        String lastUserId = userRepository.findLastUserId();
        String newUserId = idGenerate.generateNextUserId(lastUserId);

        User user = User.builder()
                .id(newUserId)
                .username(stockManagerDto.getUsername())
                .password(passwordEncoder.encode(stockManagerDto.getPassword()))
                .createdAt(new Date())
                .role(SystemUserRole.STOCK_MANAGER)
                .build();

        userRepository.save(user);

        StockManager stockManager = StockManager.builder()
                .stockManagerId(newStockManagerId)
                .fullName(stockManagerDto.getFullName())
                .email(stockManagerDto.getEmail())
                .phoneNumber(stockManagerDto.getPhoneNumber())
                .basicSalary(stockManagerDto.getBasicSalary())
                .status("Active")
                .user(user)
                .build();

        stockManagerRepository.save(stockManager);
        return "Stock Manager registered successfully";
    }

    @Transactional
    @Override
    public String updateStockManager(StockManagerDto stockManagerDto){
        if (stockManagerDto == null|| stockManagerDto.getId()==null) {
            throw new IllegalArgumentException("Update Stock Manager DTO cannot be null");
        }

        StockManager existingStockManager = stockManagerRepository.findById(stockManagerDto.getId())
                .orElseThrow(() -> new RuntimeException("Stock Manager not found"));

        existingStockManager.setFullName(stockManagerDto.getFullName());
        existingStockManager.setEmail(stockManagerDto.getEmail());
        existingStockManager.setPhoneNumber(stockManagerDto.getPhoneNumber());
        existingStockManager.setBasicSalary(stockManagerDto.getBasicSalary());
        existingStockManager.setStatus(stockManagerDto.getStatus());

        User existingUser = existingStockManager.getUser();
        existingUser.setUsername(stockManagerDto.getUsername());
        if (stockManagerDto.getPassword() != null && !stockManagerDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(stockManagerDto.getPassword()));
        }

        userRepository.save(existingUser);
        stockManagerRepository.save(existingStockManager);
        return "Stock Manager updated successfully";
    }

    @Transactional
    @Override
    public void deleteStockManager(String id) {
        StockManager stockManager = stockManagerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Stock Manager not found"));

        User linkedUser = stockManager.getUser();
//        stockManager.setUser(null);
        stockManagerRepository.save(stockManager);

        stockManagerRepository.delete(stockManager);
        if (linkedUser != null) {
            userRepository.delete(linkedUser);
        }
    }

    @Override
    public List<StockManagerDto> getAllStockManagers() {
        List<StockManager> allStockManagers = stockManagerRepository.findAll();
        if (allStockManagers.isEmpty()) {
            throw new ResourceNotFound("No Stock Managers found");
        }

        return allStockManagers.stream().map(sm -> {
            StockManagerDto dto = new StockManagerDto();
            dto.setId(sm.getStockManagerId());
            dto.setFullName(sm.getFullName());
            dto.setEmail(sm.getEmail());
            dto.setPhoneNumber(sm.getPhoneNumber());
            dto.setStatus(sm.getStatus());
            if (sm.getUser() != null) {
                dto.setUsername(sm.getUser().getUsername());
                dto.setRole(sm.getUser().getRole().name());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<StockManagerDto> searchStockManager(String keyword) {
        if (keyword==null){
            throw new IllegalArgumentException("Keyword cannot be null");
        }

        List<StockManager> allStockManagers = stockManagerRepository.findStockManagerByFullNameContainingIgnoreCase(keyword);

        if (allStockManagers.isEmpty()) {
            throw new ResourceNotFound("No Stock Managers found with the given keyword");
        }

        return allStockManagers.stream().map(sm -> {
            StockManagerDto dto = new StockManagerDto();
            dto.setId(sm.getStockManagerId());
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
    public void changeStockManagerStatus(String id) {
        if (!stockManagerRepository.existsById(id)) {
            throw new ResourceNotFound("Stock Manager not found");
        }

        if (id == null) {
            throw new IllegalArgumentException("Stock Manager ID cannot be null");
        }

        stockManagerRepository.updateStockManagerStatus(id);
    }


}
