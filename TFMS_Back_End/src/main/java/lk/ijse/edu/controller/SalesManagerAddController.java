package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.SalesManagerDto;
import lk.ijse.edu.service.SalesManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salesManager")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SalesManagerAddController {
    private final SalesManagerService salesManagerService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<String>> registerSalesManager(@RequestBody SalesManagerDto dto) {
        return new ResponseEntity<>(new APIResponse<>(
                201, "Sales Manager Registered Successfully", salesManagerService.saveSalesManager(dto)
        ), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<APIResponse<String>> updateSalesManager(@RequestBody SalesManagerDto dto) {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Sales Manager Updated Successfully", salesManagerService.updateSalesManager(dto)
        ));
    }

    @PutMapping(value = "/delete" , params = "id")
    public ResponseEntity<APIResponse<String>> deleteSalesManager(@RequestParam("id") String id) {
        salesManagerService.deleteSalesManager(id);
        return ResponseEntity.ok(new APIResponse<>(
                200, "Sales Manager Deleted Successfully", null
        ));
    }

    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<List<SalesManagerDto>>> getAllSalesManagers() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Sales Managers Retrieved Successfully", salesManagerService.getAllSalesManagers()
        ));
    }

    @GetMapping("search/{keyword}")
    public ResponseEntity<APIResponse<List<SalesManagerDto>>>searchSalesManager(@PathVariable("keyword") String keyword) {
        List<SalesManagerDto> salesManagerDTOS = salesManagerService.searchSalesManager(keyword);
        return ResponseEntity.ok(new APIResponse<>(
                200,
                "",
                salesManagerDTOS
        ));
    }

    @PatchMapping("/changeStatus/{id}")
    public ResponseEntity<APIResponse<String>> changeSalesManagerStatus(@PathVariable("id") String id) {
        salesManagerService.changeSalesManagerStatus(id);
        return ResponseEntity.ok(new APIResponse<>(
                200, "Sales Manager Status Changed Successfully", null
        ));
    }
}
