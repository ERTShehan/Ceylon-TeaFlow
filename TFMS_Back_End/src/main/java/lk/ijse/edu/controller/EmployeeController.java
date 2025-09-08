package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.EmployeeDto;
import lk.ijse.edu.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<String>> registerEmployee(@RequestBody EmployeeDto dto) {
        return new ResponseEntity<>(new APIResponse<>(
                201, "Employee Registered Successfully", employeeService.saveEmployee(dto)
        ), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<APIResponse<String>> updateEmployee(@RequestBody EmployeeDto dto) {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Employee Updated Successfully", employeeService.updateEmployee(dto)
        ));
    }

    @PutMapping(value = "/delete" , params = "id")
    public ResponseEntity<APIResponse<String>> deleteEmployee(@RequestParam("id") String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(new APIResponse<>(
                200, "Employee Deleted Successfully", null
        ));
    }

    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<List<EmployeeDto>>> getAllEmployees() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Employees Retrieved Successfully", employeeService.getAllEmployees()
        ));
    }

    @GetMapping("search/{keyword}")
    public ResponseEntity<APIResponse<List<EmployeeDto>>>searchEmployee(@PathVariable("keyword") String keyword) {
        List<EmployeeDto> employeeDTOS = employeeService.searchEmployee(keyword);
        return ResponseEntity.ok(new APIResponse<>(
                200,
                "",
                employeeDTOS
        ));
    }
}
