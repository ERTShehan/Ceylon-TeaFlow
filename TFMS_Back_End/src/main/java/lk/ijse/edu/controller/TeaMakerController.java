package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.RegisterTeaMakerDto;
import lk.ijse.edu.service.TeaMakerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teaMaker")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TeaMakerController {
    private final TeaMakerServiceImpl teaMakerServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<APIResponse> registerTeaMaker(@RequestBody RegisterTeaMakerDto dto) {
        return new ResponseEntity<>(new APIResponse(
                201, "Tea Maker Registered Successfully", teaMakerServiceImpl.saveTeaMaker(dto)
        ), HttpStatus.CREATED);
    }
}
