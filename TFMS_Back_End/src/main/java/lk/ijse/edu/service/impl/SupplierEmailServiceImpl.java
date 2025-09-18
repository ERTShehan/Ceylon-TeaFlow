package lk.ijse.edu.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lk.ijse.edu.service.SupplierEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplierEmailServiceImpl implements SupplierEmailService {
    private final JavaMailSender mailSender;

    @Override
    public void sendTeaLeafRecordEmail(String to, String date, String teaCardNumber, String supplierName, String grossWeight, String sackWeight, String moistureWeight, String netWeight, String quality) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Tea Leaf Record - Ceylon TeaFlow");

            String emailContent = """
                    <h2>Ceylon TeaFlow - Daily Tea Leaf Record</h2>
                    <p>Dear %s,</p>
                    <p>Your tea leaf supply record has been successfully saved on <b>%s</b>.</p>
                    <table border="1" cellpadding="8" cellspacing="0" style="border-collapse:collapse;">
                      <tr><th>Tea Card No</th><td>%s</td></tr>
                      <tr><th>Supplier Name</th><td>%s</td></tr>
                      <tr><th>Gross Weight</th><td>%s kg</td></tr>
                      <tr><th>Sack Weight</th><td>%s kg</td></tr>
                      <tr><th>Moisture Weight</th><td>%s kg</td></tr>
                      <tr><th>Net Weight</th><td>%s kg</td></tr>
                      <tr><th>Quality</th><td>%s</td></tr>
                    </table>
                    <br/>
                    <p>Thank you for your contribution!</p>
                    """.formatted(
                    supplierName,
                    date,
                    teaCardNumber,
                    supplierName,
                    grossWeight,
                    sackWeight,
                    moistureWeight,
                    netWeight,
                    quality
            );

            helper.setText(emailContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendTeaLeafRecordUpdateEmail(String to, String date, String teaCardNumber, String supplierName, String grossWeight, String sackWeight, String moistureWeight, String netWeight, String quality) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Tea Leaf Record - Ceylon TeaFlow");

            String emailContent = """
                    <h2>Ceylon TeaFlow - Daily Tea Leaf Record</h2>
                    <p>Dear %s,</p>
                    <p>This message is user Tea Leaf Supplier count update message or Email repeat error solve message</p>
                    <p>Your tea leaf supply record has been successfully Update on <b>%s</b>.</p>
                    <table border="1" cellpadding="8" cellspacing="0" style="border-collapse:collapse;">
                      <tr><th>Tea Card No</th><td>%s</td></tr>
                      <tr><th>Supplier Name</th><td>%s</td></tr>
                      <tr><th>Gross Weight</th><td>%s kg</td></tr>
                      <tr><th>Sack Weight</th><td>%s kg</td></tr>
                      <tr><th>Moisture Weight</th><td>%s kg</td></tr>
                      <tr><th>Net Weight</th><td>%s kg</td></tr>
                      <tr><th>Quality</th><td>%s</td></tr>
                    </table>
                    <br/>
                    <p>Thank you for your contribution!</p>
                    """.formatted(
                    supplierName,
                    date,
                    teaCardNumber,
                    supplierName,
                    grossWeight,
                    sackWeight,
                    moistureWeight,
                    netWeight,
                    quality
            );

            helper.setText(emailContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
