package lk.ijse.edu.controller;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.edu.dto.*;
import lk.ijse.edu.entity.TeaLeafSupplier;
import lk.ijse.edu.repository.TeaLeafSupplierRepository;
import lk.ijse.edu.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.IOException;
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
    private final SupplierBillService supplierBillService;

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

    @GetMapping(value = "/getMonthlyBillRecord", params = {"year", "month"})
    public ResponseEntity<APIResponse<MonthlyBillRecordDto>> getMonthlyBillRecord(
            Principal principal,
            @RequestParam int year,
            @RequestParam int month) {

        String username = principal.getName();
        TeaLeafSupplier supplier = teaLeafSupplierRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        MonthlyBillRecordDto dto = supplierBillService.getMonthlyBill(supplier.getSupplierId(), year, month);
        return ResponseEntity.ok(new APIResponse<>(200, "Monthly Bill Retrieved", dto));
    }

    @GetMapping(value = "/downloadMonthlyBill", params = {"year", "month"})
    public void downloadMonthlyBill(HttpServletResponse response,
                                    Principal principal,
                                    @RequestParam int year,
                                    @RequestParam int month) throws IOException {

        String username = principal.getName();
        TeaLeafSupplier supplier = teaLeafSupplierRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        MonthlyBillRecordDto dto = supplierBillService.getMonthlyBill(supplier.getSupplierId(), year, month);

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=MonthlyBill-" + year + "-" + month + ".pdf";
        response.setHeader(headerKey, headerValue);

        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, response.getOutputStream());

        doc.open();

        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, Color.GREEN.darker());
        Paragraph title = new Paragraph("Ceylon TeaFlow - Monthly Tea Bill", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        doc.add(title);
        doc.add(new Paragraph(" "));

        Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
        Font cellFont = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(90);
        infoTable.setSpacingBefore(10);
        infoTable.setSpacingAfter(20);
        infoTable.setWidths(new float[]{3, 5});

        addInfoRow(infoTable, "Supplier Name", dto.getSupplierName(), cellFont);
        addInfoRow(infoTable, "Tea Card Number", dto.getTeaCardNumber(), cellFont);
        addInfoRow(infoTable, "Year", String.valueOf(dto.getYear()), cellFont);
        addInfoRow(infoTable, "Month", String.valueOf(dto.getMonth()), cellFont);
        doc.add(infoTable);

        PdfPTable billTable = new PdfPTable(2);
        billTable.setWidthPercentage(90);
        billTable.setSpacingBefore(10);
        billTable.setWidths(new float[]{4, 4});

        PdfPCell header1 = new PdfPCell(new Phrase("Description", headerFont));
        header1.setBackgroundColor(new Color(34, 139, 34));
        header1.setHorizontalAlignment(Element.ALIGN_CENTER);
        header1.setPadding(6);
        billTable.addCell(header1);

        PdfPCell header2 = new PdfPCell(new Phrase("Amount", headerFont));
        header2.setBackgroundColor(new Color(34, 139, 34));
        header2.setHorizontalAlignment(Element.ALIGN_CENTER);
        header2.setPadding(6);
        billTable.addCell(header2);

        addBillRow(billTable, "Total Weight", dto.getTotalWeight() + " kg", cellFont);
        addBillRow(billTable, "Unit Price", "LKR " + dto.getUnitPrice(), cellFont);
        addBillRow(billTable, "Advance Payment", "LKR " + dto.getAdvancePayment(), cellFont);
        addBillRow(billTable, "Tea Packet Cost", "LKR " + dto.getTeaPacketCost(), cellFont);

        Font totalFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.RED.darker());
        PdfPCell totalLabel = new PdfPCell(new Phrase("Net Total Price", totalFont));
        totalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
        totalLabel.setPadding(8);
        billTable.addCell(totalLabel);

        PdfPCell totalValue = new PdfPCell(new Phrase("LKR " + dto.getTotalPrice(), totalFont));
        totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalValue.setPadding(8);
        billTable.addCell(totalValue);

        doc.add(billTable);

        doc.close();
    }

    private PdfPCell getInfoCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        return cell;
    }

    private void addInfoRow(PdfPTable table, String key, String value, Font font) {
        table.addCell(getInfoCell(key + ":", font));
        table.addCell(getInfoCell(value, font));
    }

    private void addBillRow(PdfPTable table, String desc, String amount, Font font) {
        PdfPCell c1 = new PdfPCell(new Phrase(desc, font));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setPadding(6);
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase(amount, font));
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c2.setPadding(6);
        table.addCell(c2);
    }
}
