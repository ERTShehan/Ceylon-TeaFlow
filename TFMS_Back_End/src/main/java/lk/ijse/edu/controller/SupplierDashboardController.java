package lk.ijse.edu.controller;

import lk.ijse.edu.dto.*;
import lk.ijse.edu.entity.TeaLeafSupplier;
import lk.ijse.edu.repository.TeaLeafSupplierRepository;
import lk.ijse.edu.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/supplier")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SupplierDashboardController {
    private final TeaProductService teaProductService;
    private final AdvancePaymentService advancePaymentService;
    private final TeaCountService teaCountService;
    private final TeaLeafSupplierRepository teaLeafSupplierRepository;
    private final TeaPacketRequestService teaPacketRequestService;
    private final SupplierTotalPriceService supplierTotalPriceService;

    @GetMapping("/teaProduction")
    public ResponseEntity<APIResponse<List<TeaProductDto>>> getTeaProducts() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Products Retrieved Successfully", teaProductService.getAllTeaProducts()
        ));
    }

    @PostMapping("/applyAdvance")
    public ResponseEntity<APIResponse<String>> applyForAdvance(
            @RequestBody AdvancePaymentsDto advancePaymentsDto,
            Principal principal
    ) {
        String username = principal.getName(); // login una user name (DB 'users' table eke username)
        String response = advancePaymentService.saveAdvancePayment(advancePaymentsDto, username);

        return new ResponseEntity<>(new APIResponse<>(
                200, "Advance Application Submitted Successfully", response
        ), HttpStatus.CREATED);
    }


    @GetMapping("/getAllAdvances")
    public ResponseEntity<APIResponse<List<AdvancePaymentsDto>>> getAdvanceRequests(Principal principal) {
        String username = principal.getName();
        List<AdvancePaymentsDto> requests = advancePaymentService.getAdvancePaymentsForSupplier(username);

        return ResponseEntity.ok(new APIResponse<>(
                200,
                "Advance Requests Retrieved Successfully",
                requests
        ));
    }

    @GetMapping("/supplierCalendarData")
    public ResponseEntity<APIResponse<List<DaySupplyDto>>> getSupplierCalendarData(
            @RequestParam(defaultValue = "3") int monthsBack,
            Principal principal) {

        String username = principal.getName();

        TeaLeafSupplier supplier = teaLeafSupplierRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Supplier not found for username: " + username));

        List<DaySupplyDto> data = teaCountService.getSupplierCalendarData(supplier.getSupplierId(), monthsBack);

        APIResponse<List<DaySupplyDto>> resp =
                new APIResponse<>(200, "Supplier calendar data retrieved", data);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/getSupplierMonthlyTotal")
    public ResponseEntity<APIResponse<Double>> getMonthlySupplyTotal(Principal principal) {
        String username = principal.getName();

        TeaLeafSupplier supplier = teaLeafSupplierRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Supplier not found for username: " + username));

        double total = teaCountService.getSupplierMonthlyTotal(supplier.getSupplierId());

        APIResponse<Double> resp = new APIResponse<>(200, "Monthly total retrieved", total);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/applyPacket")
    public ResponseEntity<APIResponse<ApplyPacketResponseDto>> applyPacket(
            @RequestParam String productId,
            Principal principal) {

        String username = principal.getName();
        ApplyPacketResponseDto dto = teaPacketRequestService.applyPacket(productId, username);

        return ResponseEntity.ok(
                new APIResponse<>(200, "SUCCESS", dto)
        );
    }

    @GetMapping("/totalTeaPacketRequestsMonth")
    public ResponseEntity<APIResponse<Long>> getTotalTeaPacketRequestsThisMonth(Principal principal) {
        String username = principal.getName();

        TeaLeafSupplier supplier = teaLeafSupplierRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Supplier not found for username: " + username));

        long totalRequests = teaPacketRequestService.getTotalTeaPacketRequestsThisMonth(supplier.getSupplierId());

        return ResponseEntity.ok(
                new APIResponse<>(200, "Total tea packet requests for this month retrieved", totalRequests)
        );
    }

    @GetMapping("/getAllTeaPacketRequests")
    public ResponseEntity<APIResponse<List<TeaPacketRequestDto>>> getAllTeaPacketRequests(Principal principal) {
        String username = principal.getName();

        TeaLeafSupplier supplier = teaLeafSupplierRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Supplier not found for username: " + username));

        List<TeaPacketRequestDto> requests = teaPacketRequestService.getAllRequestsBySupplier(supplier.getSupplierId());

        return ResponseEntity.ok(
                new APIResponse<>(200, "All tea packet requests retrieved", requests)
        );
    }

    @GetMapping(value = "/getSupplierMonthlyTotal", params = {"year", "month"})
    public ResponseEntity<APIResponse<MonthlySupplySummaryDto>> getSupplierMonthlyTotal(Principal principal,
            @RequestParam int year,
            @RequestParam int month
            ) {

        String username = principal.getName();
        TeaLeafSupplier supplier = teaLeafSupplierRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        MonthlySupplySummaryDto summary =
                supplierTotalPriceService.getSupplierMonthlyTotalPrice(supplier.getSupplierId(), year, month);

        return ResponseEntity.ok(
                new APIResponse<>(200, "Monthly supply summary retrieved successfully", summary)
        );
    }


}
