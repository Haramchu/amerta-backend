package propensi.amesta.service;

import java.util.List;

import propensi.amesta.payload.response.Sales.SalesReceiptResponseDTO;

public interface NotaPenjualanService {
    SalesReceiptResponseDTO getSalesReceiptById(String id);
    List<SalesReceiptResponseDTO> getAllSalesReceipts();
}
