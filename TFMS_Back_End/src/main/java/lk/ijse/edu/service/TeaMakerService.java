package lk.ijse.edu.service;

import lk.ijse.edu.dto.TeaMakerDto;

import java.util.List;

public interface TeaMakerService {
    String saveTeaMaker(TeaMakerDto teaMakerDto);
    String updateTeaMaker(TeaMakerDto teaMakerDto);
    String deleteTeaMaker(String id);
    List<TeaMakerDto> getAllTeaMakers();
    List<TeaMakerDto> searchTeaMaker(String keyword);
    void changeTeaMakerStatus(String id);
}
