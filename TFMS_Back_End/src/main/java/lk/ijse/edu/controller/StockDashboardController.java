package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.StockResponseDto;
import lk.ijse.edu.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stockDashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockDashboardController {
    private final StockService stockService;

    @GetMapping("/getStockLevels")
    public ResponseEntity<APIResponse<List<StockResponseDto>>> getStockLevels(){
        return ResponseEntity.ok(new APIResponse<>(
                200, "Done", stockService.getAllStockLevels()
        ));
    }
}
