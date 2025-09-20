package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.AddNewStockDto;
import lk.ijse.edu.dto.StockResponseDto;
import lk.ijse.edu.service.StockService;
import lk.ijse.edu.service.TeaProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stockDashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockDashboardController {
    private final StockService stockService;
    private final TeaProductService teaProductService;

    @GetMapping("/getStockLevels")
    public ResponseEntity<APIResponse<List<StockResponseDto>>> getStockLevels(){
        return ResponseEntity.ok(new APIResponse<>(
                200, "Done", stockService.getAllStockLevels()
        ));
    }

    @GetMapping("/loadTeaProductInDropdown")
    public ResponseEntity<APIResponse<List<String>>> loadTeaProductInDropdown() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Product Retrieved Successfully", teaProductService.getTeaProductNames()
        ));
    }

    @PostMapping("/addTeaInStock")
    public ResponseEntity<APIResponse<String>> addTeaProductInStock(@RequestBody AddNewStockDto addNewStockDto){
        return ResponseEntity.ok(new APIResponse<>(
                200, "Stock update successfully", stockService.addTeaProduct(addNewStockDto)
        ));
    }
}