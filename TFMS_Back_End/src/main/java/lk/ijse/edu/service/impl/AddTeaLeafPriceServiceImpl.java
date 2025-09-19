package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.AddTeaLeafPriceDto;
import lk.ijse.edu.entity.TeaLeafPrice;
import lk.ijse.edu.exception.ResourceNotFound;
import lk.ijse.edu.repository.AddTeaLeafPriceRepository;
import lk.ijse.edu.service.AddTeaLeafPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddTeaLeafPriceServiceImpl implements AddTeaLeafPriceService {
    private final AddTeaLeafPriceRepository addTeaLeafPriceRepository;

    private String generateNextLeafPriceId(String lastId) {
        if (lastId == null) return "T-000-001";

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

        return String.format("T-%03d-%03d", major, minor);
    }

    @Override
    public String addTeaLeafPrice(AddTeaLeafPriceDto addTeaLeafPriceDto) {
        String effectiveMonth = String.valueOf(addTeaLeafPriceDto.getYearMonth());

        boolean exists = addTeaLeafPriceRepository.existsByEffectiveMonth(effectiveMonth);
        if (exists) {
            throw new IllegalArgumentException("Tea leaf price for " + effectiveMonth + " already exists!");
        }

        String lastId = addTeaLeafPriceRepository.findLastTeaLeafPriceId();
        String newTeaLeafPriceId = generateNextLeafPriceId(lastId);

        TeaLeafPrice teaLeafPrice = TeaLeafPrice.builder()
                .teaLeafPriceId(newTeaLeafPriceId)
                .effectiveMonth(String.valueOf(addTeaLeafPriceDto.getYearMonth()))
                .pricePerKg(addTeaLeafPriceDto.getPricePerKg())
                .build();

        addTeaLeafPriceRepository.save(teaLeafPrice);
        return "Add Tea Leaf Price Successfully";
    }

    @Override
    public String updateTeaLeafPrice(AddTeaLeafPriceDto addTeaLeafPriceDto) {
        if (addTeaLeafPriceDto == null || addTeaLeafPriceDto.getId() == null) {
            throw new IllegalArgumentException("Tea Leaf Price DTO or ID cannot be null");
        }

        TeaLeafPrice existingTeaLeafPrice = addTeaLeafPriceRepository.findById(addTeaLeafPriceDto.getId())
                .orElseThrow(() -> new RuntimeException("Tea Leaf Price not found"));

        existingTeaLeafPrice.setEffectiveMonth(String.valueOf(addTeaLeafPriceDto.getYearMonth()));
        existingTeaLeafPrice.setPricePerKg(addTeaLeafPriceDto.getPricePerKg());

        addTeaLeafPriceRepository.save(existingTeaLeafPrice);
        return "Tea Leaf Price Updated Successfully";
    }

    @Override
    public List<AddTeaLeafPriceDto> getAllTeaLeafPrices() {
        List<TeaLeafPrice> allTeaLeafPrices = addTeaLeafPriceRepository.findAll();
        if (allTeaLeafPrices.isEmpty()) {
            throw new ResourceNotFound("No Tea Leaf Prices Found");
        }

        return allTeaLeafPrices.stream().map(lf -> {
            AddTeaLeafPriceDto dto = new AddTeaLeafPriceDto();
            dto.setId(lf.getTeaLeafPriceId());
            dto.setYearMonth(YearMonth.parse(lf.getEffectiveMonth()));
            dto.setPricePerKg(lf.getPricePerKg());
            return dto;
        }).collect(Collectors.toList());
    }
}
