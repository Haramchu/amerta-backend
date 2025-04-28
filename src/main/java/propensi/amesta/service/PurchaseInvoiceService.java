package propensi.amesta.service;

import java.math.BigDecimal;

import propensi.amesta.model.Purchase.PurchaseInvoice;

public interface PurchaseInvoiceService {
    PurchaseInvoice generateInvoice(String nomorNota, String id, BigDecimal jumlahTagihan, String status);
}

