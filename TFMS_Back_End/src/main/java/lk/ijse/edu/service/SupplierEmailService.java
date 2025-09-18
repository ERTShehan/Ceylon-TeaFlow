package lk.ijse.edu.service;

public interface SupplierEmailService {
    void sendTeaLeafRecordEmail(String to, String date, String teaCardNumber, String supplierName, String grossWeight, String sackWeight, String moistureWeight, String netWeight, String quality);
    void sendTeaLeafRecordUpdateEmail(String to, String date, String teaCardNumber, String supplierName, String grossWeight, String sackWeight, String moistureWeight, String netWeight, String quality);
}
