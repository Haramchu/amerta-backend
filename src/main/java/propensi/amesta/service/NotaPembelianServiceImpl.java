package propensi.amesta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.amesta.model.Purchase.PurchaseReceipt;
import propensi.amesta.payload.response.Purchase.PurchaseReceiptResponseDTO;
import propensi.amesta.repository.Purchase.PurchaseReceiptDb;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotaPembelianServiceImpl implements NotaPembelianService{
    @Autowired
    private PurchaseReceiptDb purchaseReceiptDb;

    @Override
    public PurchaseReceiptResponseDTO getPurchaseReceiptById(String id) {
        PurchaseReceipt receipt = purchaseReceiptDb.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt tidak ditemukan"));

        return receiptToResponseDTO(receipt);
    }

    @Override
    public List<PurchaseReceiptResponseDTO> getAllPurchaseReceipts() {
        List<PurchaseReceipt> receipts = purchaseReceiptDb.findAll();
        List<PurchaseReceiptResponseDTO> responseDTOList = new ArrayList<>();
        for (PurchaseReceipt receipt : receipts) {
            responseDTOList.add(receiptToResponseDTO(receipt));
        }
        return responseDTOList;
    }

    private PurchaseReceiptResponseDTO receiptToResponseDTO(PurchaseReceipt receipt) {
        return new PurchaseReceiptResponseDTO(
                receipt.getId(),
                receipt.getPurchaseOrder().getId(),
                receipt.getReceiptDate(),
                receipt.getAmountPayed()
        );
    }
}
