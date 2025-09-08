package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.TeaProductDto;
import lk.ijse.edu.service.TeaProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerDashboardController {
    private final TeaProductService teaProductService;

    @GetMapping("/teaProduction")
    public ResponseEntity<APIResponse<List<TeaProductDto>>> getTeaProducts() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Products Retrieved Successfully", teaProductService.getAllTeaProducts()
        ));
    }
}
