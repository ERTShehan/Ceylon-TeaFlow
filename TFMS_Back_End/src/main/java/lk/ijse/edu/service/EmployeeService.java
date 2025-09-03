package lk.ijse.edu.service;

import lk.ijse.edu.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {
    String saveEmployee(EmployeeDto employeeDto);
    String updateEmployee(EmployeeDto employeeDto);
    void deleteEmployee(String id);
    List<EmployeeDto> getAllEmployees();
    List<EmployeeDto> searchEmployee(String keyword);
}
