package propensi.amesta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import propensi.amesta.payload.response.BaseResponseDTO;
import propensi.amesta.payload.response.Sales.SalesInvoiceResponseDTO;
import propensi.amesta.service.SalesInvoiceService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/sales-invoice")
public class SalesInvoiceController {

    @Autowired
    private SalesInvoiceService salesInvoiceService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceById(@PathVariable String id) {
        BaseResponseDTO<SalesInvoiceResponseDTO> response = new BaseResponseDTO<>();
        try {
            SalesInvoiceResponseDTO invoice = salesInvoiceService.getInvoiceById(id);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Invoice ditemukan.");
            response.setData(invoice);
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("Invoice tidak ditemukan: " + e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/viewall")
    public ResponseEntity<?> getAllInvoices() {
        BaseResponseDTO<List<SalesInvoiceResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<SalesInvoiceResponseDTO> list = salesInvoiceService.getAllInvoices();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Semua invoice berhasil ditemukan.");
            response.setData(list);
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan: " + e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}