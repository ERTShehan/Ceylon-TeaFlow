package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.DaySupplyDto;
import lk.ijse.edu.dto.QualityDistributionDto;
import lk.ijse.edu.dto.TeaLeafCountDto;
import lk.ijse.edu.dto.TopSupplierDto;
import lk.ijse.edu.entity.QualityAssessment;
import lk.ijse.edu.entity.TeaLeafCount;
import lk.ijse.edu.entity.TeaLeafSupplier;
import lk.ijse.edu.exception.ResourceNotFound;
import lk.ijse.edu.repository.TeaCountRepository;
import lk.ijse.edu.repository.TeaLeafSupplierRepository;
import lk.ijse.edu.service.SupplierEmailService;
import lk.ijse.edu.service.TeaCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeaCountServiceImpl implements TeaCountService {
    private final TeaCountRepository teaCountRepository;
    private final TeaLeafSupplierRepository teaLeafSupplierRepository;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final SupplierEmailService supplierEmailService;

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

        supplierEmailService.sendTeaLeafRecordEmail(
                supplier.getEmail(),
                teaLeafCount.getDate(),
                teaLeafCountDto.getTeaCardNumber(),
                supplier.getFirstName() + " " + supplier.getLastName(),
                teaLeafCountDto.getGrossWeight(),
                teaLeafCountDto.getSackWeight(),
                teaLeafCountDto.getMoistureWeight(),
                teaLeafCountDto.getNetWeight(),
                teaLeafCountDto.getQuality()
        );

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
                .orElseThrow(() -> new RuntimeException("Tea Leaf Count Id not found"));

//        existing.setTeaCardNumber(dto.getTeaCardNumber());
//        existing.setSupplierName(dto.getSupplierName());
        existing.setGrossWeight(dto.getGrossWeight());
        existing.setSackWeight(dto.getSackWeight());
        existing.setMoistureWeight(dto.getMoistureWeight());
        existing.setNetWeight(dto.getNetWeight());
        existing.setNote(dto.getNote());

        teaCountRepository.save(existing);

        TeaLeafSupplier supplier = existing.getSupplier();
        if (supplier != null && supplier.getEmail() != null) {
            supplierEmailService.sendTeaLeafRecordUpdateEmail(
                    supplier.getEmail(),
                    existing.getDate(),
                    existing.getTeaCardNumber(),
                    supplier.getFirstName() + " " + supplier.getLastName(),
                    existing.getGrossWeight(),
                    existing.getSackWeight(),
                    existing.getMoistureWeight(),
                    existing.getNetWeight(),
                    String.valueOf(existing.getQuality())
            );
        }

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

    @Override
    public List<TopSupplierDto> getTopSuppliers() {
        List<Object[]> raw = teaCountRepository.findTopSuppliersNative();
        return raw.stream()
                .map(r -> new TopSupplierDto(
                        (String) r[0],
                        (String) r[1],
                        (String) r[2],
                        r[3] != null ? Double.valueOf(r[3].toString()) : 0.0
                ))
                .collect(Collectors.toList());
    }

    private double parseNetWeight(String netStr) {
        if (netStr == null) return 0.0;
        // extract first number-like substring
        Pattern p = Pattern.compile("([0-9]+(?:\\.[0-9]+)?)");
        Matcher m = p.matcher(netStr);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(1));
            } catch (NumberFormatException ex) {
                return 0.0;
            }
        }
        return 0.0;
    }

    @Override
    public List<DaySupplyDto> getSupplierCalendarData(String supplierId, int monthsBack) {
        // calculate start and end dates
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        YearMonth startMonth = currentMonth.minusMonths(monthsBack);

        LocalDate startDate = startMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        String fromStr = startDate.format(fmt); // "yyyy-MM-dd"
        String toStr = endDate.format(fmt);

        List<TeaLeafCount> counts = teaCountRepository.findBySupplierSupplierIdAndDateBetween(supplierId, fromStr, toStr);

        // aggregate by date string (exact match of date value)
        Map<String, Double> agg = new TreeMap<>(); // sorted by date
        for (TeaLeafCount t : counts) {
            String d = t.getDate();
            double val = parseNetWeight(t.getNetWeight());
            agg.put(d, agg.getOrDefault(d, 0.0) + val);
        }

        // Ensure every date in range exists in result (optional: include zeros)
        List<DaySupplyDto> result = new ArrayList<>();
        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            String ds = cursor.format(fmt);
            double total = agg.getOrDefault(ds, 0.0);
            result.add(new DaySupplyDto(ds, total));
            cursor = cursor.plusDays(1);
        }

        // return only dates for the months range, sorted
        return result;
    }

    @Override
    public double getSupplierMonthlyTotal(String supplierId) {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        String fromStr = startDate.format(fmt); // yyyy-MM-dd
        String toStr = endDate.format(fmt);

        List<TeaLeafCount> counts =
                teaCountRepository.findBySupplierSupplierIdAndDateBetween(supplierId, fromStr, toStr);

        return counts.stream()
                .mapToDouble(c -> parseNetWeight(c.getNetWeight()))
                .sum();
    }

}
