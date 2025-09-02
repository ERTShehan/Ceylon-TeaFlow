package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.StockManagerDto;
import lk.ijse.edu.service.StockManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping(value = "/delete" , params = "id")
    public ResponseEntity<APIResponse<String>> deleteStockManager(@RequestParam("id") String id) {
        stockManagerService.deleteStockManager(id);
        return ResponseEntity.ok(new APIResponse<>(
                200, "Stock Manager Deleted Successfully", null
        ));
    }

    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<List<StockManagerDto>>> getAllStockManagers() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Stock Managers Retrieved Successfully", stockManagerService.getAllStockManagers()
        ));
    }

    @GetMapping("search/{keyword}")
    public ResponseEntity<APIResponse<List<StockManagerDto>>>searchStockManager(@PathVariable("keyword") String keyword) {
        List<StockManagerDto> stockManagerDTOS = stockManagerService.searchStockManager(keyword);
        return ResponseEntity.ok(new APIResponse<>(
                200,
                "",
                stockManagerDTOS
        ));
    }

    @PatchMapping("/changeStatus/{id}")
    public ResponseEntity<APIResponse<String>> changeStockManagerStatus(@PathVariable("id") String id) {
        stockManagerService.changeStockManagerStatus(id);
        return ResponseEntity.ok(new APIResponse<>(
                200, "Stock Manager Status Changed Successfully", null
        ));
    }
}
