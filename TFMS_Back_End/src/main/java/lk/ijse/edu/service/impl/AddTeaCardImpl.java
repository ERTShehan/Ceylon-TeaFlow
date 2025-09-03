package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.TeaCardDto;
import lk.ijse.edu.entity.TeaCard;
import lk.ijse.edu.repository.TeaCardRepository;
import lk.ijse.edu.service.AddTeaCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddTeaCardImpl implements AddTeaCardService {
    private final TeaCardRepository teaCardRepository;

    private String generateNextTeaCardId(String lastId) {
        if (lastId == null || lastId.isEmpty()) return "TC-00001";

        String numericStr = lastId.substring(3); // Assumes prefix is always "TC-"
        int number = Integer.parseInt(numericStr);

        number++;

        if (number > 99999) {
            throw new IllegalStateException("ID range exceeded. Maximum allowed is 99999.");
        }

        return String.format("TC-%05d", number);
    }

    @Override
    public String saveTeaCard(TeaCardDto teaCardDto) {
        if (teaCardRepository.existsByNumber(teaCardDto.getNumber())){
            throw new RuntimeException("Tea Card Number already exists");
        }

        String lastId = teaCardRepository.findLastTeaCardId();
        String newTeaCardId = generateNextTeaCardId(lastId);

        TeaCard teaCard = TeaCard.builder()
                .id(newTeaCardId)
                .number(teaCardDto.getNumber())
                .name(teaCardDto.getName())
                .issuedAt(new Date())
                .build();
        teaCardRepository.save(teaCard);
        return "Tea Card saved successfully";
    }

    @Override
    public void deleteTeaCard(String id) {

    }

    @Override
    public List<TeaCardDto> getAllTeaCards() {
        return List.of();
    }

    @Override
    public List<TeaCardDto> searchTeaCard(String keyword) {
        return List.of();
    }
}
