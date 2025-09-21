package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.TeaLeafCountDto;
import lk.ijse.edu.dto.TopSupplierDto;
import lk.ijse.edu.repository.NormalCustomerRepository;
import lk.ijse.edu.repository.TeaLeafSupplierRepository;
import lk.ijse.edu.service.StockService;
import lk.ijse.edu.service.TeaCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/adminDashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminDashboardDataController {

    private final TeaLeafSupplierRepository teaLeafSupplierRepository;
    private final NormalCustomerRepository normalCustomerRepository;
    private final StockService stockService;
    private final TeaCountService teaCountService;

    @GetMapping("/totalSuppliers")
    public ResponseEntity<APIResponse<Long>> getTotalSupplierCount() {
        long count = teaLeafSupplierRepository.count();
        return ResponseEntity.ok(new APIResponse<>(
                200, "Total Supplier Count Retrieved Successfully", count
        ));
    }

    @GetMapping("/totalNormalCustomers")
    public ResponseEntity<APIResponse<Long>> getTotalNormalCustomerCount() {
        long count = normalCustomerRepository.count();
        return ResponseEntity.ok(new APIResponse<>(
                200, "Total Normal Customer Count Retrieved Successfully", count
        ));
    }

    @GetMapping("/getTotalStockQuantity")
    public ResponseEntity<APIResponse<Long>> getTotalStockQuantity() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Total stock quantity retrieved successfully", stockService.getTotalStockQuantity()
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

    @GetMapping("/getAllTeaLeafCounts")
    public ResponseEntity<APIResponse<List<TeaLeafCountDto>>> getAllTeaLeafCounts() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "All Tea Leaf Counts Retrieved Successfully", teaCountService.getAllTeaLeafCounts()
        ));
    }
}
