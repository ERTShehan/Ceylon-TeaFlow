package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.TeaMakerDto;
import lk.ijse.edu.service.TeaMakerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teaMaker")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TeaMakerController {
    private final TeaMakerService teaMakerService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<String>> registerTeaMaker(@RequestBody TeaMakerDto dto) {
        return new ResponseEntity<>(new APIResponse<>(
                201, "Tea Maker Registered Successfully", teaMakerService.saveTeaMaker(dto)
        ), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<APIResponse<String>> updateTeaMaker(@RequestBody TeaMakerDto dto) {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Maker Updated Successfully", teaMakerService.updateTeaMaker(dto)
        ));
    }

    @PutMapping(value = "/delete" , params = "id")
    public ResponseEntity<APIResponse<String>> deleteTeaMaker(@RequestParam("id") String id) {
        teaMakerService.deleteTeaMaker(id);
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Maker Deleted Successfully", null
        ));
    }

    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<List<TeaMakerDto>>> getAllTeaMakers() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Makers Retrieved Successfully", teaMakerService.getAllTeaMakers()
        ));
    }
}
