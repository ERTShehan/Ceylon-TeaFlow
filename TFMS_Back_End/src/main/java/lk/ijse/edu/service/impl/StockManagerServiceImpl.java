package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.StockManagerDto;
import lk.ijse.edu.entity.StockManager;
import lk.ijse.edu.entity.SystemUserRole;
import lk.ijse.edu.entity.User;
import lk.ijse.edu.repository.StockManagerRepository;
import lk.ijse.edu.repository.UserRepository;
import lk.ijse.edu.service.StockManagerService;
import lk.ijse.edu.util.IdGenerate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
    public String saveStockManager(StockManagerDto stockManagerDto) {
        if (userRepository.existsByUsername(stockManagerDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (stockManagerDto ==null){
            throw new IllegalArgumentException("Register Stock Manager DTO cannot be null");
        }

        String lastId = stockManagerRepository.findLastStockManagerId();
        String newStockManagerId = generateNextStockManagerId(lastId);

        String lastUserId = userRepository.findLastUserId();
        String newUserId = idGenerate.generateNextUserId(lastUserId);

        User user = User.builder()
                .id(newUserId)
                .username(stockManagerDto.getUsername())
                .password(passwordEncoder.encode(stockManagerDto.getPassword()))
                .createdAt(new Date())
                .role(SystemUserRole.TEAM_MAKER)
                .build();

        userRepository.save(user);

        StockManager stockManager = StockManager.builder()
                .StockManagerId(newStockManagerId)
                .fullName(stockManagerDto.getFullName())
                .email(stockManagerDto.getEmail())
                .phoneNumber(stockManagerDto.getPhoneNumber())
                .basicSalary(stockManagerDto.getBasicSalary())
                .status(stockManagerDto.getStatus())
                .user(user)
                .build();

        stockManagerRepository.save(stockManager);
        return "Stock Manager registered successfully";
    }
}
