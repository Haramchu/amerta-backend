package propensi.amesta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import propensi.amesta.model.Sales.SalesInvoice;
import propensi.amesta.service.SalesInvoiceService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales-invoice")
public class SalesInvoiceController {

    @Autowired
    private SalesInvoiceService salesInvoiceService;

    @PostMapping("/create")
    public ResponseEntity<?> createInvoice(@RequestParam("nomorNota") String nomorNota,
                                       @RequestParam("jumlahTagihan") BigDecimal jumlahTagihan,
                                       @RequestParam(value = "statusPembayaran", defaultValue = "Belum Dibayar") String statusPembayaran) {
        String id = "INV-" + UUID.randomUUID().toString().substring(0, 8);
        SalesInvoice invoice = salesInvoiceService.generateInvoice(nomorNota, id, statusPembayaran, jumlahTagihan);
        return ResponseEntity.ok(invoice);
    }
}