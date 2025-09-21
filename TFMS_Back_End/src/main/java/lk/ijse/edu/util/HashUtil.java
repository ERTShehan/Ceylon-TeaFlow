package lk.ijse.edu.util;

import java.math.BigDecimal;
import java.security.MessageDigest;

public class HashUtil {
    public static String md5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(text.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateHash(String merchantId, String orderId,
                                      BigDecimal amount, String currency, String merchantSecret) {
        String amountStr = String.format("%.2f", amount);
        String inner = md5(merchantSecret).toUpperCase();
        return md5(merchantId + orderId + amountStr + currency + inner).toUpperCase();
    }
}
