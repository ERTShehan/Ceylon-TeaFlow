package lk.ijse.edu.service.impl;

import lk.ijse.edu.service.PayHereService;
import lk.ijse.edu.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PayHereServiceImpl implements PayHereService {
    @Value("${payhere.merchant.id}") private String merchantId;
    @Value("${payhere.merchant.secret}") private String merchantSecret;
    @Value("${payhere.return.url}") private String returnUrl;
    @Value("${payhere.cancel.url}") private String cancelUrl;
    @Value("${payhere.notify.url}") private String notifyUrl;
    @Value("${payhere.mode}") private String mode;

    public Map<String,Object> createPayment(String orderId, String itemName,
                                            BigDecimal amount, String firstName,
                                            String lastName, String email,
                                            String phone, String address,
                                            String city) {

        String hash = HashUtil.generateHash(merchantId, orderId, amount, "LKR", merchantSecret);

        Map<String,Object> map = new HashMap<>();
        map.put("sandbox", "sandbox".equalsIgnoreCase(mode));
        map.put("merchant_id", merchantId);
        map.put("return_url", returnUrl);
        map.put("cancel_url", cancelUrl);
        map.put("notify_url", notifyUrl);
        map.put("order_id", orderId);
        map.put("items", itemName);
        map.put("currency", "LKR");
        map.put("amount", String.format("%.2f", amount));
        map.put("hash", hash);
        map.put("first_name", firstName);
        map.put("last_name", lastName);
        map.put("email", email);
        map.put("phone", phone);
        map.put("address", address);
        map.put("city", city);
        map.put("country", "Sri Lanka");
        return map;
    }

    public boolean verifyIPN(Map<String,String> params) {
        String localMd5 = HashUtil.md5(
                params.get("merchant_id")
                        + params.get("order_id")
                        + params.get("payhere_amount")
                        + params.get("payhere_currency")
                        + params.get("status_code")
                        + HashUtil.md5(merchantSecret).toUpperCase()
        ).toUpperCase();
        return localMd5.equals(params.get("md5sig"));
    }

}
