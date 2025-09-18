package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.QualityDistributionDto;
import lk.ijse.edu.dto.TeaLeafCountDto;
import lk.ijse.edu.dto.TopSupplierDto;
import lk.ijse.edu.service.TeaCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teaMakerDashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TeaMakerDashboardController {
    private final TeaCountService teaCountService;

    @PostMapping("/addTeaLeafCount")
    public ResponseEntity<APIResponse<String>> addTeaCount(@RequestBody TeaLeafCountDto teaLeafCountDto) {
        return new ResponseEntity<>(new APIResponse<>(
                201, "Tea Leaf Count Added & Email Sent Successfully", teaCountService.addTeaLeafCount(teaLeafCountDto)
        ), HttpStatus.CREATED);
    }


    @GetMapping("/getSupplierByCard/{cardNumber}")
    public ResponseEntity<APIResponse<String>> getSupplierByCard(@PathVariable String cardNumber) {
        String supplierName = teaCountService.findSupplierNameByCard(cardNumber);
        if (supplierName == null) {
            return new ResponseEntity<>(new APIResponse<>(404, "Supplier Not Found", null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new APIResponse<>(200, "Supplier Found", supplierName), HttpStatus.OK);
    }

    @GetMapping("/getAllTodayTeaLeafCounts")
    public ResponseEntity<APIResponse<List<TeaLeafCountDto>>> getAllTodayTeaLeafCounts() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Today's Tea Leaf Counts Retrieved Successfully", teaCountService.getAllTodayTeaLeafCounts()
        ));
    }

    @PutMapping("/updateTeaLeafCount")
    public ResponseEntity<APIResponse<String>> updateTeaLeafCount(@RequestBody TeaLeafCountDto dto) {
        return ResponseEntity.ok(new APIResponse<>(
                200,
                "Tea Leaf Count updated Successfully",
                teaCountService.updateTeaLeafCount(dto) // Email auto yawanna service ekedi
        ));
    }



    @GetMapping("/getAllTeaLeafCounts")
    public ResponseEntity<APIResponse<List<TeaLeafCountDto>>> getAllTeaLeafCounts() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "All Tea Leaf Counts Retrieved Successfully", teaCountService.getAllTeaLeafCounts()
        ));
    }

    @GetMapping("/todayQualityDistribution")
    public ResponseEntity<APIResponse<QualityDistributionDto>> todayQualityDistribution() {
        return ResponseEntity.ok(
                new APIResponse<>(200, "Today's Quality Distribution Retrieved Successfully",
                        teaCountService.getTodayQualityDistribution())
        );
    }

//    @GetMapping("/monthlyQualityTrend")
//    public ResponseEntity<APIResponse<MonthlyQualityTrendDto>> monthlyQualityTrend() {
//        return ResponseEntity.ok(
//                new APIResponse<>(200, "Monthly Quality Trend Retrieved Successfully",
//                        teaCountService.getMonthlyQualityTrend())
//        );
//    }

    @GetMapping("/getTopSuppliers")
    public ResponseEntity<APIResponse<List<TopSupplierDto>>> getTopSuppliers() {
        List<TopSupplierDto> data = teaCountService.getTopSuppliers();
        return ResponseEntity.ok(new APIResponse<>(
                200, "Top Suppliers Retrieved Successfully", data
        ));
    }

    @GetMapping("/todayRecordsCount")
    public ResponseEntity<APIResponse<Long>> todayRecordsCount() {
        long count = teaCountService.getAllTodayTeaLeafCounts().size();
        return ResponseEntity.ok(new APIResponse<>(
                200, "Today's Records Count Retrieved Successfully", count
        ));
    }

    @GetMapping("/totalWeightThisMonth")
    public ResponseEntity<APIResponse<Double>> totalWeightThisMonth() {
        double totalWeight = teaCountService.getAllTeaLeafCounts().stream()
                .filter(count -> count.getDate().startsWith(java.time.LocalDate.now().withDayOfMonth(1).toString().substring(0, 7)))
                .mapToDouble(count -> {
                    try {
                        return Double.parseDouble(count.getNetWeight());
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                })
                .sum();
        return ResponseEntity.ok(new APIResponse<>(
                200, "Total Weight This Month Retrieved Successfully", totalWeight
        ));
    }

    @GetMapping("/bestSupplierToday")
    public ResponseEntity<APIResponse<TopSupplierDto>> bestSupplierToday() {
        List<TopSupplierDto> topSuppliers = teaCountService.getTopSuppliers();
        if (topSuppliers.isEmpty()) {
            return ResponseEntity.ok(new APIResponse<>(
                    200, "No Suppliers Found Today", null
            ));
        }
        return ResponseEntity.ok(new APIResponse<>(
                200, "Best Supplier Today Retrieved Successfully", topSuppliers.get(0)
        ));
    }
}