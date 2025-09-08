package lk.ijse.edu.service.impl;

import lk.ijse.edu.dto.TeaCardDto;
import lk.ijse.edu.entity.TeaCard;
import lk.ijse.edu.entity.TeaLeafSupplier;
import lk.ijse.edu.entity.User;
import lk.ijse.edu.exception.ResourceNotFound;
import lk.ijse.edu.repository.TeaCardRepository;
import lk.ijse.edu.repository.TeaLeafSupplierRepository;
import lk.ijse.edu.repository.UserRepository;
import lk.ijse.edu.service.AddTeaCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddTeaCardImpl implements AddTeaCardService {
    private final TeaCardRepository teaCardRepository;
    private final TeaLeafSupplierRepository teaLeafSupplierRepository;
    private final UserRepository userRepository;

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

    @Transactional
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

    @Transactional
    @Override
    public String deleteTeaCard(String id) {
        TeaCard teaCard = teaCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Tea Card not found with ID: " + id));

        // Check if tea card is associated with a supplier
        if (teaCard.getSupplier() != null) {
            TeaLeafSupplier supplier = teaCard.getSupplier();
            User user = supplier.getUser();

            // Remove associations
            teaCard.setSupplier(null);
            supplier.setTeaCard(null);
            supplier.setUser(null);
            user.setTeaLeafSupplier(null);

            // Delete supplier and user
            teaLeafSupplierRepository.delete(supplier);
            userRepository.delete(user);
        }

        // Delete the tea card
        teaCardRepository.delete(teaCard);

        return "Tea Card deleted successfully";
    }


    @Override
    public List<TeaCardDto> getAllTeaCards() {
        List<TeaCard> allTeaCards = teaCardRepository.findAll();
        if (allTeaCards.isEmpty()) {
            throw new ResourceNotFound("No tea card Found");
        }

        return allTeaCards.stream().map(teaCard -> {
            TeaCardDto teaCardDto = new TeaCardDto();
            teaCardDto.setId(teaCard.getId());
            teaCardDto.setNumber(teaCard.getNumber());
            teaCardDto.setName(teaCard.getName());
            teaCardDto.setIssuedAt(String.valueOf(teaCard.getIssuedAt()));
            return teaCardDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TeaCardDto> searchTeaCard(String keyword) {
        if (keyword==null){
            throw new IllegalArgumentException("Keyword cannot be null");
        }

        List<TeaCard> allTeaCards = teaCardRepository.findTeaCardByNameContainingIgnoreCase(keyword);

        if (allTeaCards.isEmpty()){
            throw new ResourceNotFound("No Tea Maker Found");
        }

        return allTeaCards.stream().map(teaCard -> {
            TeaCardDto teaCardDto = new TeaCardDto();
            teaCardDto.setId(teaCard.getId());
            teaCardDto.setNumber(teaCard.getNumber());
            teaCardDto.setName(teaCard.getName());
            teaCardDto.setIssuedAt(String.valueOf(teaCard.getIssuedAt()));
            return teaCardDto;
        }).collect(Collectors.toList());
    }
}
