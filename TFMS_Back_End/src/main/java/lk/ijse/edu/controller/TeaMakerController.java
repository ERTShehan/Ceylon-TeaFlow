package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.RegisterTeaMakerDto;
import lk.ijse.edu.service.TeaMakerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teaMaker")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TeaMakerController {
    private final TeaMakerService teaMakerService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse> registerTeaMaker(@RequestBody RegisterTeaMakerDto dto) {
        return new ResponseEntity<>(new APIResponse(
                201, "Tea Maker Registered Successfully", teaMakerService.saveTeaMaker(dto)
        ), HttpStatus.CREATED);
    }
}
