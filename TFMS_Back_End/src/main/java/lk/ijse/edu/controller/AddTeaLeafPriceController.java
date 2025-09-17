package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.AddTeaLeafPriceDto;
import lk.ijse.edu.service.AddTeaLeafPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addTeaLeafPrice")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AddTeaLeafPriceController {
    private final AddTeaLeafPriceService addTeaLeafPriceService;

    @PostMapping("/add")
    public ResponseEntity<APIResponse<String>> addTeaLeafPrice(@RequestBody AddTeaLeafPriceDto dto) {
        return new ResponseEntity<>(new APIResponse<>(
                201, "Tea Leaf Price Added Successfully", addTeaLeafPriceService.addTeaLeafPrice(dto)
        ), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<APIResponse<String>> updateTeaLeafPrice(@RequestBody AddTeaLeafPriceDto dto) {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Leaf Price Updated Successfully", addTeaLeafPriceService.updateTeaLeafPrice(dto)
        ));
    }

    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<List<AddTeaLeafPriceDto>>> getAllTeaLeafPrices() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Leaf Prices Retrieved Successfully", addTeaLeafPriceService.getAllTeaLeafPrices()
        ));
    }
}
