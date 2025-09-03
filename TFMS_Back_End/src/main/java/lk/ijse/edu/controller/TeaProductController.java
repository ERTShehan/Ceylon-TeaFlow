package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.TeaProductDto;
import lk.ijse.edu.service.TeaProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teaProduct")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TeaProductController {
    private final TeaProductService teaProductService;

    @PostMapping("/add")
    public ResponseEntity<APIResponse<String>> registerTeaProduct(@RequestBody TeaProductDto dto) {
        return new ResponseEntity<>(new APIResponse<>(
                201, "Tea Product Registered Successfully", teaProductService.saveTeaProduct(dto)
        ), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<APIResponse<String>> updateTeaProduct(@RequestBody TeaProductDto dto) {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Product Updated Successfully", teaProductService.updateTeaProduct(dto)
        ));
    }

    @PutMapping(value = "/delete" , params = "id")
    public ResponseEntity<APIResponse<String>> deleteTeaProduct(@RequestParam("id") String id) {
        teaProductService.deleteTeaProduct(id);
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Product Deleted Successfully", null
        ));
    }

    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<List<TeaProductDto>>> getAllTeaProducts() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Products Retrieved Successfully", teaProductService.getAllTeaProducts()
        ));
    }

    @GetMapping("search/{keyword}")
    public ResponseEntity<APIResponse<List<TeaProductDto>>>searchTeaProduct(@PathVariable("keyword") String keyword) {
        List<TeaProductDto> teaProductDTOS = teaProductService.searchTeaProduct(keyword);
        return ResponseEntity.ok(new APIResponse<>(
                200,
                "",
                teaProductDTOS
        ));
    }
}
