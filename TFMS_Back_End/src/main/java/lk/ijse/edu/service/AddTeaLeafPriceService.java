package lk.ijse.edu.service;

import lk.ijse.edu.dto.AddTeaLeafPriceDto;

import java.util.List;

public interface AddTeaLeafPriceService {
    String addTeaLeafPrice(AddTeaLeafPriceDto addTeaLeafPriceDto);
    String updateTeaLeafPrice(AddTeaLeafPriceDto addTeaLeafPriceDto);
    List<AddTeaLeafPriceDto> getAllTeaLeafPrices();
}
