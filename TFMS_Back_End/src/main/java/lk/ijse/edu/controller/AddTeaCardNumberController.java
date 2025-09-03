package lk.ijse.edu.controller;

import lk.ijse.edu.dto.APIResponse;
import lk.ijse.edu.dto.TeaCardDto;
import lk.ijse.edu.service.AddTeaCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teaCard")
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

    @PutMapping(value = "/delete", params = "id")
    public ResponseEntity<APIResponse<String>> deleteTeaCard(@RequestParam("id") String id) {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Card Deleted Successfully", addTeaCardService.deleteTeaCard(id)
        ));
    }

    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<java.util.List<TeaCardDto>>> getAllTeaCards() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Cards Retrieved Successfully", addTeaCardService.getAllTeaCards()
        ));
    }

    @GetMapping("search/{keyword}")
    public ResponseEntity<APIResponse<List<TeaCardDto>>> searchTeaCard(@PathVariable("keyword") String keyword) {
        List<TeaCardDto> teaCardDTOS = addTeaCardService.searchTeaCard(keyword);
        return ResponseEntity.ok(new APIResponse<>(
                200,
                "",
                teaCardDTOS
        ));
    }
}
