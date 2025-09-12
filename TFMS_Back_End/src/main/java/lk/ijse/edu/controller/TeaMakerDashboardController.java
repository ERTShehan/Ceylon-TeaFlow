package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.QualityDistributionDto;
import lk.ijse.edu.dto.TeaLeafCountDto;
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
                201, "Tea Leaf Count Added Successfully", teaCountService.addTeaLeafCount(teaLeafCountDto)
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
                200, "Tea Leaf Count updated Successfully", teaCountService.updateTeaLeafCount(dto)
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

}