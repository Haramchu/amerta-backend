package propensi.amesta.service;

import java.util.List;

import propensi.amesta.payload.response.Purchase.PurchaseReceiptResponseDTO;

public interface NotaPembelianService {
    PurchaseReceiptResponseDTO getPurchaseReceiptById(String id);
    List<PurchaseReceiptResponseDTO> getAllPurchaseReceipts();
}
