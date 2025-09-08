package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.AdvancePaymentsDto;
import lk.ijse.edu.dto.TeaProductDto;
import lk.ijse.edu.service.TeaProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplier")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SupplierDashboardController {
    private final TeaProductService teaProductService;

    @GetMapping("/teaProduction")
    public ResponseEntity<APIResponse<List<TeaProductDto>>> getTeaProducts() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Products Retrieved Successfully", teaProductService.getAllTeaProducts()
        ));
    }

    @PostMapping("/applyAdvance")
    public ResponseEntity<APIResponse<String>> applyForAdvance(@RequestBody AdvancePaymentsDto advancePaymentsDto) {
        return new ResponseEntity<>(new APIResponse<>(
                200, "Advance Application Submitted Successfully", null
        ), HttpStatus.CREATED);
    }
}
