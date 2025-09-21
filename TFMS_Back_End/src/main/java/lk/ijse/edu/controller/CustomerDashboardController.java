package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.CustomerDetailsDto;
import lk.ijse.edu.dto.TeaProductDto;
import lk.ijse.edu.entity.NormalCustomer;
import lk.ijse.edu.repository.NormalCustomerRepository;
import lk.ijse.edu.service.TeaProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerDashboardController {
    private final TeaProductService teaProductService;
    private final NormalCustomerRepository normalCustomerRepository;

    @GetMapping("/teaProduction")
    public ResponseEntity<APIResponse<List<TeaProductDto>>> getTeaProducts() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Products Retrieved Successfully", teaProductService.getAllTeaProducts()
        ));
    }

    @GetMapping("/getCustomerDetails")
    public ResponseEntity<APIResponse<CustomerDetailsDto>> getCustomerDetails(Principal principal) {
        String username = principal.getName();

        NormalCustomer customer = normalCustomerRepository
                .findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        String fullName = customer.getFirstName() + " " + customer.getLastName();

        CustomerDetailsDto dto = new CustomerDetailsDto(fullName, customer.getAddress());

        return ResponseEntity.ok(
                new APIResponse<>(200, "Customer Details Retrieved Successfully", dto)
        );
    }

}
