package lk.ijse.edu.service;

import java.math.BigDecimal;
import java.util.Map;

public interface PayHereService {
    Map<String, Object> createPayment(String orderId, String itemName,
                                      BigDecimal amount, String firstName,
                                      String lastName, String email,
                                      String phone, String address,
                                      String city);
    boolean verifyIPN(Map<String, String> params);
}
