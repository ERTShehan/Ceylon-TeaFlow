package lk.ijse.edu.service;

import lk.ijse.edu.dto.TeaCardDto;

import java.util.List;

public interface AddTeaCardService {
    String saveTeaCard(TeaCardDto teaCardDto);
    String deleteTeaCard(String id);
    List<TeaCardDto> getAllTeaCards();
    List<TeaCardDto> searchTeaCard(String keyword);
}
