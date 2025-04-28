package propensi.amesta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import propensi.amesta.payload.response.BaseResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchaseInvoiceResponseDTO;
import propensi.amesta.service.PurchaseInvoiceService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-invoice")
public class PurchaseInvoiceController {

    @Autowired
    private PurchaseInvoiceService purchaseInvoiceService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceById(@PathVariable String id) {
        BaseResponseDTO<PurchaseInvoiceResponseDTO> response = new BaseResponseDTO<>();
        try {
            PurchaseInvoiceResponseDTO invoice = purchaseInvoiceService.getInvoiceById(id);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Invoice berhasil ditemukan.");
            response.setData(invoice);
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("Invoice tidak ditemukan: " + e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/viewall")
    public ResponseEntity<?> getAllInvoices() {
        BaseResponseDTO<List<PurchaseInvoiceResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<PurchaseInvoiceResponseDTO> invoices = purchaseInvoiceService.getAllInvoices();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Daftar semua invoice berhasil ditemukan.");
            response.setData(invoices);
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Gagal mendapatkan daftar invoice: " + e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
