package lk.ijse.edu.controller;

import lk.ijse.edu.service.PayHereService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payhere")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PayHereController {
    private final PayHereService payHereService;

    @PostMapping("/create")
    public ResponseEntity<Map<String,Object>> create(@RequestParam String productName,
                                                     @RequestParam Double price) {
        String orderId = "ORD-" + System.currentTimeMillis();
        Map<String,Object> data = payHereService.createPayment(
                orderId,
                productName,
                BigDecimal.valueOf(price),
                "Tharindu", "Shehan",
                "test@gmail.com",
                "0770000000",
                "Galle Road",
                "Galle"
        );
        return ResponseEntity.ok(data);
    }

    @PostMapping(value="/notify", consumes= MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> notify(@RequestParam Map<String,String> params) {
        if (payHereService.verifyIPN(params) && "2".equals(params.get("status_code"))) {
            // TODO: Update order status in DB => Paid
            System.out.println("Payment SUCCESS for Order: " + params.get("order_id"));
        } else {
            System.out.println("Payment Failed/Invalid");
        }
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/return")
    public String success() { return "Payment Success!"; }

    @GetMapping("/cancel")
    public String cancel() { return "Payment Cancelled!"; }

}
