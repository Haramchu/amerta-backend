package propensi.amesta.service;

import java.util.List;

import propensi.amesta.model.Sales.SalesInvoice;
import propensi.amesta.payload.response.Sales.SalesInvoiceResponseDTO;

public interface SalesInvoiceService {
    public SalesInvoiceResponseDTO getInvoiceById(String id);
    public List<SalesInvoiceResponseDTO> getAllInvoices();
}
