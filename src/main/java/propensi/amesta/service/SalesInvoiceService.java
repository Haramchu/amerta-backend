package propensi.amesta.service;

import propensi.amesta.model.Sales.SalesInvoice;

public interface SalesInvoiceService {
    SalesInvoice generateInvoice(String nomorNota, String id, String status, java.math.BigDecimal total);
}
