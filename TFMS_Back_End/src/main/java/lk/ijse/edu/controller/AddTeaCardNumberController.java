package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.TeaCardDto;
import lk.ijse.edu.service.AddTeaCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/addTeaCard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AddTeaCardNumberController {
    private final AddTeaCardService addTeaCardService;

    @PostMapping("/add")
    public ResponseEntity<APIResponse<String>> addTeaCard(@RequestBody TeaCardDto dto) {
        return new ResponseEntity<>(new APIResponse<>(
                201, "Tea Card Numbers Added Successfully", addTeaCardService.saveTeaCard(dto)
        ), HttpStatus.CREATED);
    }
}
