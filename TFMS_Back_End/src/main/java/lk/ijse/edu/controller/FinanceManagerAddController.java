package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.FinanceManagerDto;
import lk.ijse.edu.service.FinanceManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/financeManager")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FinanceManagerAddController {
    private final FinanceManagerService financeManagerService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<String>> registerFinanceManager(@RequestBody FinanceManagerDto dto) {
        return new ResponseEntity<>(new APIResponse<>(
                201, "Finance Manager Registered Successfully", financeManagerService.saveFinanceManager(dto)
        ), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<APIResponse<String>> updateFinanceManager(@RequestBody FinanceManagerDto dto) {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Finance Manager Updated Successfully", financeManagerService.updateFinanceManager(dto)
        ));
    }

    @PutMapping(value = "/delete" , params = "id")
    public ResponseEntity<APIResponse<String>> deleteFinanceManager(@RequestParam("id") String id) {
        financeManagerService.deleteFinanceManager(id);
        return ResponseEntity.ok(new APIResponse<>(
                200, "Finance Manager Deleted Successfully", null
        ));
    }

    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<List<FinanceManagerDto>>> getAllFinanceManagers() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Finance Managers Retrieved Successfully", financeManagerService.getAllFinanceManagers()
        ));
    }

    @GetMapping("search/{keyword}")
    public ResponseEntity<APIResponse<List<FinanceManagerDto>>>searchFinanceManager(@PathVariable("keyword") String keyword) {
        List<FinanceManagerDto> financeManagerDTOS = financeManagerService.searchFinanceManager(keyword);
        return ResponseEntity.ok(new APIResponse<>(
                200,
                "",
                financeManagerDTOS
        ));
    }

    @PatchMapping("/changeStatus/{id}")
    public ResponseEntity<APIResponse<String>> changeFinanceManagerStatus(@PathVariable("id") String id) {
        financeManagerService.changeFinanceManagerStatus(id);
        return ResponseEntity.ok(new APIResponse<>(
                200, "Finance Manager Status Changed Successfully", null
        ));
    }
}
