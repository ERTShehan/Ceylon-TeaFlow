package lk.ijse.edu.service;

import lk.ijse.edu.dto.QualityDistributionDto;
import lk.ijse.edu.dto.TeaLeafCountDto;
import lk.ijse.edu.dto.TopSupplierDto;

import java.util.List;

public interface TeaCountService {
    String addTeaLeafCount(TeaLeafCountDto teaLeafCountDto);
    String findSupplierNameByCard(String cardNumber);
    List<TeaLeafCountDto> getAllTodayTeaLeafCounts();
    String updateTeaLeafCount(TeaLeafCountDto dto);
    List<TeaLeafCountDto> getAllTeaLeafCounts();
    QualityDistributionDto getTodayQualityDistribution();
    List<TopSupplierDto> getTopSuppliers();
}
