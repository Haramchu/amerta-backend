package propensi.amesta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import propensi.amesta.model.Purchase.PurchaseInvoice;
import propensi.amesta.service.PurchaseInvoiceService;

import java.math.BigDecimal;
import java.util.UUID;


@RestController
@RequestMapping("/api/purchase-invoice")
public class PurchaseInvoiceController {

    @Autowired
    private PurchaseInvoiceService purchaseInvoiceService;

    @PostMapping("/create")
    public ResponseEntity<?> createInvoice(@RequestParam("nomorNota") String nomorNota,
                                           @RequestParam("jumlahTagihan") BigDecimal jumlahTagihan,
                                           @RequestParam(value = "statusPembayaran", defaultValue = "Belum Dibayar") String statusPembayaran) {
        String id = "PINV-" + UUID.randomUUID().toString().substring(0, 8);
        PurchaseInvoice invoice = purchaseInvoiceService.generateInvoice(nomorNota, id, jumlahTagihan, statusPembayaran);
        return ResponseEntity.ok(invoice);
    }
}

