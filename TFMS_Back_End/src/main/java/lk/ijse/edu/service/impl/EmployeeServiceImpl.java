package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.EmployeeDto;
import lk.ijse.edu.entity.Employee;
import lk.ijse.edu.entity.TeaProduct;
import lk.ijse.edu.exception.ResourceNotFound;
import lk.ijse.edu.repository.EmployeeRepository;
import lk.ijse.edu.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    private String generateNextEmployeeId(String lastId) {
        if (lastId == null || lastId.isEmpty()) return "EM-00001";

        String numericStr = lastId.substring(3);
        int number = Integer.parseInt(numericStr);

        number++;

        if (number > 99999) {
            throw new IllegalStateException("ID range exceeded. Maximum allowed is 99999.");
        }

        return String.format("EM-%05d", number);
    }

    @Transactional
    @Override
    public String saveEmployee(EmployeeDto employeeDto) {
        if (employeeDto == null) {
            throw new IllegalArgumentException("Employee DTO cannot be null");
        }

        String lastId = employeeRepository.findLastEmployeeId();
        String newEmployeeId = generateNextEmployeeId(lastId);

        Employee employee = Employee.builder()
                .id(newEmployeeId)
                .name(employeeDto.getName())
                .address(employeeDto.getAddress())
                .basicSalary(employeeDto.getBasicSalary())
                .phone(employeeDto.getPhone())
                .department(employeeDto.getDepartment())
                .build();
        employeeRepository.save(employee);
        return "Employee saved successfully";
    }

    @Transactional
    @Override
    public String updateEmployee(EmployeeDto employeeDto) {
        if (employeeDto == null|| employeeDto.getId()==null) {
            throw new IllegalArgumentException("Update Employee DTO cannot be null");
        }
        Employee existingEmployees = employeeRepository.findById(employeeDto.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        existingEmployees.setName(employeeDto.getName());
        existingEmployees.setAddress(employeeDto.getAddress());
        existingEmployees.setBasicSalary(employeeDto.getBasicSalary());
        existingEmployees.setPhone(employeeDto.getPhone());
        existingEmployees.setDepartment(employeeDto.getDepartment());
        employeeRepository.save(existingEmployees);
        return "Employee updated successfully";
    }

    @Transactional
    @Override
    public void deleteEmployee(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Employee not found"));
        employeeRepository.delete(employee);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> allEmployees = employeeRepository.findAll();
        return allEmployees.stream().map(employee -> {
            EmployeeDto employeeDto = new EmployeeDto();
            employeeDto.setId(employee.getId());
            employeeDto.setName(employee.getName());
            employeeDto.setAddress(employee.getAddress());
//            employeeDto.setBasicSalary(employee.getBasicSalary());
            employeeDto.setPhone(employee.getPhone());
            employeeDto.setDepartment(employee.getDepartment());
            return employeeDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> searchEmployee(String keyword) {
        if (keyword==null){
            throw new IllegalArgumentException("Keyword cannot be null");
        }

        List<Employee> allEmployees = employeeRepository.findEmployeeByNameContainingIgnoreCase(keyword);
        if (allEmployees.isEmpty()){
            throw new ResourceNotFound("No Employee Found");
        }

        return allEmployees.stream().map(employee -> {
            EmployeeDto employeeDto = new EmployeeDto();
            employeeDto.setId(employee.getId());
            employeeDto.setName(employee.getName());
            employeeDto.setAddress(employee.getAddress());
            employeeDto.setPhone(employee.getPhone());
            employeeDto.setDepartment(employee.getDepartment());
            return employeeDto;
        }).collect(Collectors.toList());
    }
}
