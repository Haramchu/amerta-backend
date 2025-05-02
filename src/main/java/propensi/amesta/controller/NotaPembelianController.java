package propensi.amesta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import propensi.amesta.payload.response.BaseResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchaseReceiptResponseDTO;
import propensi.amesta.service.NotaPembelianService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-receipt")
public class NotaPembelianController {

    @Autowired
    private NotaPembelianService notaPembelianService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getPurchaseReceiptById(@PathVariable String id) {
        BaseResponseDTO<PurchaseReceiptResponseDTO> response = new BaseResponseDTO<>();
        try {
            PurchaseReceiptResponseDTO receipt = notaPembelianService.getPurchaseReceiptById(id);
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
    public ResponseEntity<?> getAllPurchaseReceipts() {
        BaseResponseDTO<List<PurchaseReceiptResponseDTO>> response = new BaseResponseDTO<>();
        try {
            List<PurchaseReceiptResponseDTO> receipts = notaPembelianService.getAllPurchaseReceipts();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Daftar semua nota pembelian berhasil ditemukan.");
            response.setData(receipts);
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Gagal mendapatkan daftar nota pembelian: " + e.getMessage());
            response.setTimestamp(new Date());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}