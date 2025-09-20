package lk.ijse.edu.controller;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.edu.dto.*;
import lk.ijse.edu.service.StockService;
import lk.ijse.edu.service.TeaProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/stockDashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockDashboardController {
    private final StockService stockService;
    private final TeaProductService teaProductService;

    @GetMapping("/getStockLevels")
    public ResponseEntity<APIResponse<List<StockResponseDto>>> getStockLevels(){
        return ResponseEntity.ok(new APIResponse<>(
                200, "Done", stockService.getGroupedStockLevels()
        ));
    }

    @GetMapping("/loadTeaProductInDropdown")
    public ResponseEntity<APIResponse<List<String>>> loadTeaProductInDropdown() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Tea Product Retrieved Successfully", teaProductService.getTeaProductNames()
        ));
    }

    @PostMapping("/addTeaInStock")
    public ResponseEntity<APIResponse<String>> addTeaProductInStock(@RequestBody AddNewStockDto addNewStockDto){
        return ResponseEntity.ok(new APIResponse<>(
                200, "Stock update successfully", stockService.addNewStock(addNewStockDto)
        ));
    }

    @GetMapping("/getStockHistory")
    public ResponseEntity<APIResponse<Page<StockHistoryDto>>> getStockHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "ALL") String filter) {

        return ResponseEntity.ok(new APIResponse<>(
                200, "Done", stockService.getStockHistory(page, size, filter)
        ));
    }

    @GetMapping("/getTotalStockQuantity")
    public ResponseEntity<APIResponse<Long>> getTotalStockQuantity() {
        return ResponseEntity.ok(new APIResponse<>(
                200, "Total stock quantity retrieved successfully", stockService.getTotalStockQuantity()
        ));
    }

    @GetMapping("/downloadReport")
    public void downloadStockReport(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=StockReport.pdf");

        List<StockReportDto> reportData = stockService.getStockSummary();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Ceylon TeaFlow - Stock Summary Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph("Generated On: " + java.time.LocalDateTime.now()));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 2f});

        PdfPCell cell1 = new PdfPCell(new Phrase("Tea Product"));
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setBackgroundColor(new Color(200, 230, 201));
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase("Total Quantity (Kg)"));
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBackgroundColor(new Color(200, 230, 201));
        table.addCell(cell2);

        for (StockReportDto dto : reportData) {
            table.addCell(dto.getTeaName().name());
            table.addCell(dto.getTotalQuantity());
        }

        document.add(table);
        document.close();
    }
}