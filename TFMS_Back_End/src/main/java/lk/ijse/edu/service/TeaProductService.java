package lk.ijse.edu.service;

import lk.ijse.edu.dto.TeaProductDto;

import java.util.List;

public interface TeaProductService {
    String saveTeaProduct(TeaProductDto teaProductDto);
    String updateTeaProduct(TeaProductDto teaProductDto);
    void deleteTeaProduct(String id);
    List<TeaProductDto> getAllTeaProducts();
    List<TeaProductDto> searchTeaProduct(String keyword);
    List<String> getTeaProductNames();
}
