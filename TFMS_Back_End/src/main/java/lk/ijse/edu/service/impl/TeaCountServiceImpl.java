package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.QualityDistributionDto;
import lk.ijse.edu.dto.TeaLeafCountDto;
import lk.ijse.edu.entity.QualityAssessment;
import lk.ijse.edu.entity.TeaLeafCount;
import lk.ijse.edu.entity.TeaLeafSupplier;
import lk.ijse.edu.exception.ResourceNotFound;
import lk.ijse.edu.repository.TeaCountRepository;
import lk.ijse.edu.repository.TeaLeafSupplierRepository;
import lk.ijse.edu.service.TeaCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeaCountServiceImpl implements TeaCountService {
    private final TeaCountRepository teaCountRepository;
    private final TeaLeafSupplierRepository teaLeafSupplierRepository;

    private String generateNextTeaCountId(String lastId) {
        if (lastId == null) return "TLC-000-001";

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

        return String.format("TLC-%03d-%03d", major, minor);
    }

    @Override
    public String addTeaLeafCount(TeaLeafCountDto teaLeafCountDto) {
        if (teaLeafCountDto == null) {
            throw new IllegalArgumentException("Tea Leaf Count DTO cannot be null");
        }

        String lastId = teaCountRepository.findLastTeaLeafCountId();
        String nextId = generateNextTeaCountId(lastId);

        TeaLeafSupplier supplier = teaLeafSupplierRepository.findByTeaCardNumber(teaLeafCountDto.getTeaCardNumber());
        if (supplier == null) {
            throw new RuntimeException("Supplier not found for Tea Card Number: " + teaLeafCountDto.getTeaCardNumber());
        }

        QualityAssessment qualityEnum;
        try {
            qualityEnum = QualityAssessment.valueOf(teaLeafCountDto.getQuality().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid quality value: " + teaLeafCountDto.getQuality());
        }

        TeaLeafCount teaLeafCount = TeaLeafCount.builder()
                .id(nextId)
                .teaCardNumber(teaLeafCountDto.getTeaCardNumber())
                .supplierName(supplier.getFirstName() + " " + supplier.getLastName())
                .grossWeight(teaLeafCountDto.getGrossWeight())
                .sackWeight(teaLeafCountDto.getSackWeight())
                .moistureWeight(teaLeafCountDto.getMoistureWeight())
                .netWeight(teaLeafCountDto.getNetWeight())
                .date(String.valueOf(LocalDate.now()))
                .time(LocalTime.now().withNano(0).toString())
                .quality(qualityEnum)
                .supplier(supplier)
                .note(teaLeafCountDto.getNote())
                .build();

        teaCountRepository.save(teaLeafCount);
        System.out.println(teaLeafCount);
        return "Tea Leaf Count Added Successfully";
    }

    @Override
    public String findSupplierNameByCard(String cardNumber) {
        TeaLeafSupplier supplier = teaLeafSupplierRepository.findByTeaCardNumber(cardNumber);
        return supplier != null ? supplier.getFirstName() + " " + supplier.getLastName() : null;
    }

    @Override
    public List<TeaLeafCountDto> getAllTodayTeaLeafCounts() {
        String today = LocalDate.now().toString();
        List<TeaLeafCount> todayCounts = teaCountRepository.findAllByToday(today);

        return todayCounts.stream().map(count -> TeaLeafCountDto.builder()
                .id(count.getId())
                .teaCardNumber(count.getTeaCardNumber())
                .supplierName(count.getSupplierName())
                .grossWeight(count.getGrossWeight())
                .sackWeight(count.getSackWeight())
                .moistureWeight(count.getMoistureWeight())
                .netWeight(count.getNetWeight())
                .date(count.getDate())
                .time(count.getTime())
                .quality(count.getQuality().name())
                .note(count.getNote())
                .build()
        ).toList();
    }

    @Override
    public String updateTeaLeafCount(TeaLeafCountDto dto) {
        TeaLeafCount existing = teaCountRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Tea Leaf Count not found"));

//        existing.setTeaCardNumber(dto.getTeaCardNumber());
//        existing.setSupplierName(dto.getSupplierName());
        existing.setGrossWeight(dto.getGrossWeight());
        existing.setSackWeight(dto.getSackWeight());
        existing.setMoistureWeight(dto.getMoistureWeight());
        existing.setNetWeight(dto.getNetWeight());
        existing.setNote(dto.getNote());

        teaCountRepository.save(existing);
        return "Tea Leaf Count Updated Successfully";
    }

    @Override
    public List<TeaLeafCountDto> getAllTeaLeafCounts() {
        List<TeaLeafCount> allTeaLeafCounts = teaCountRepository.findAll();

        if (allTeaLeafCounts.isEmpty()) {
            throw new ResourceNotFound("No Tea Leaf Count found");
        }

        return allTeaLeafCounts.stream().map(count -> TeaLeafCountDto.builder()
                .id(count.getId())
                .teaCardNumber(count.getTeaCardNumber())
                .supplierName(count.getSupplierName())
                .grossWeight(count.getGrossWeight())
                .sackWeight(count.getSackWeight())
                .moistureWeight(count.getMoistureWeight())
                .netWeight(count.getNetWeight())
                .date(count.getDate())
                .time(count.getTime())
                .quality(count.getQuality().name())
                .note(count.getNote())
                .build()
        ).toList();
    }

    @Override
    public QualityDistributionDto getTodayQualityDistribution() {
        String today = java.time.LocalDate.now().toString();

        List<TeaLeafCount> todayCounts = teaCountRepository.findByDate(today);

        if (todayCounts.isEmpty()) {
            return new QualityDistributionDto(0, 0, 0, 0);
        }

        long total = todayCounts.size();
        long excellent = todayCounts.stream().filter(c -> c.getQuality() == QualityAssessment.EXCELLENT).count();
        long good = todayCounts.stream().filter(c -> c.getQuality() == QualityAssessment.GOOD).count();
        long average = todayCounts.stream().filter(c -> c.getQuality() == QualityAssessment.AVERAGE).count();
        long poor = todayCounts.stream().filter(c -> c.getQuality() == QualityAssessment.POOR).count();

        return QualityDistributionDto.builder()
                .excellentPercentage((excellent * 100.0) / total)
                .goodPercentage((good * 100.0) / total)
                .averagePercentage((average * 100.0) / total)
                .poorPercentage((poor * 100.0) / total)
                .build();
    }

}
