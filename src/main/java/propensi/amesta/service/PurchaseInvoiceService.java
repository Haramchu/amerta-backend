package propensi.amesta.service;

import java.math.BigDecimal;
import java.util.List;

import propensi.amesta.model.Purchase.PurchaseInvoice;
import propensi.amesta.payload.response.Purchase.PurchaseInvoiceResponseDTO;

public interface PurchaseInvoiceService {
    PurchaseInvoiceResponseDTO getInvoiceById(String id);
    List<PurchaseInvoiceResponseDTO> getAllInvoices();
}

