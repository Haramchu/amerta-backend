package propensi.amesta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import propensi.amesta.payload.response.BaseResponseDTO;
import propensi.amesta.payload.response.Sales.SalesReceiptResponseDTO;
import propensi.amesta.service.NotaPenjualanService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/sales-receipt")
public class NotaPenjualanController {
    @Autowired
    private NotaPenjualanService notaPenjualanService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getSalesReceiptById(@PathVariable String id) {
        BaseResponseDTO<SalesReceiptResponseDTO> response = new BaseResponseDTO<>();
        try {
            SalesReceiptResponseDTO receipt = notaPenjualanService.getSalesReceiptById(id);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Receipt berhasil ditemukan.");
            response.setData(receipt);
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("Receipt tidak ditemukan: " + e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/viewall")
    public ResponseEntity<?> getAllSalesReceipts() {
        BaseResponseDTO<List<SalesReceiptResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<SalesReceiptResponseDTO> receipts = notaPenjualanService.getAllSalesReceipts();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Daftar semua nota penjualan berhasil ditemukan.");
            response.setData(receipts);
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Gagal mendapatkan daftar nota penjualan: " + e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
