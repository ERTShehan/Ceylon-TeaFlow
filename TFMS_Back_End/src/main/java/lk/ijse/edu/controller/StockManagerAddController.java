package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.StockManagerDto;
import lk.ijse.edu.service.StockManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stockManager")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockManagerAddController {
    private final StockManagerService stockManagerService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<String>> registerStockManager(@RequestBody StockManagerDto dto) {
        return new ResponseEntity<>(new APIResponse<>(
                201, "Stock Manager Registered Successfully", stockManagerService.saveStockManager(dto)
        ), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<APIResponse<String>> updateStockManager(@RequestBody StockManagerDto dto) {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Stock Manager Updated Successfully", stockManagerService.updateStockManager(dto)
        ));
    }
}
