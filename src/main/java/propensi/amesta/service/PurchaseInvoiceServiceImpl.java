package propensi.amesta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.amesta.model.Purchase.PurchaseInvoice;
import propensi.amesta.repository.Purchase.PurchaseInvoiceDb;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PurchaseInvoiceServiceImpl implements PurchaseInvoiceService {

    @Autowired
    private PurchaseInvoiceDb purchaseInvoiceDb;

    @Override
    public PurchaseInvoice generateInvoice(String nomorNota, String id, BigDecimal jumlahTagihan, String status) {
        PurchaseInvoice invoice = new PurchaseInvoice();
        invoice.setId(id);
        invoice.setNomorNota(nomorNota);
        invoice.setJumlahTagihan(jumlahTagihan);
        invoice.setStatusPembayaran(status);
        invoice.setTanggalDibuat(LocalDateTime.now());
        return purchaseInvoiceDb.save(invoice);
    }
}
