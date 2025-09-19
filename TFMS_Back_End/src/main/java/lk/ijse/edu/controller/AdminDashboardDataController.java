package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.repository.NormalCustomerRepository;
import lk.ijse.edu.repository.TeaLeafSupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adminDashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminDashboardDataController {

    private final TeaLeafSupplierRepository teaLeafSupplierRepository;
    private final NormalCustomerRepository normalCustomerRepository;

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
}
