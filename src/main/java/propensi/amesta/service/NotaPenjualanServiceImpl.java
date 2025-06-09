package propensi.amesta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.amesta.model.Sales.SalesReceipt;
import propensi.amesta.payload.response.Sales.SalesReceiptResponseDTO;
import propensi.amesta.repository.Sales.SalesReceiptDb;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotaPenjualanServiceImpl implements NotaPenjualanService {
    @Autowired
    private SalesReceiptDb salesReceiptDb;

    @Override
    public SalesReceiptResponseDTO getSalesReceiptById(String id) {
        SalesReceipt receipt = salesReceiptDb.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt tidak ditemukan"));

        return receiptToResponseDTO(receipt);
    }

    @Override
    public List<SalesReceiptResponseDTO> getAllSalesReceipts() {
        List<SalesReceipt> receipts = salesReceiptDb.findAll();
        List<SalesReceiptResponseDTO> responseDTOList = new ArrayList<>();
        for (SalesReceipt receipt : receipts) {
            responseDTOList.add(receiptToResponseDTO(receipt));
        }
        return responseDTOList;
    }

    private SalesReceiptResponseDTO receiptToResponseDTO(SalesReceipt receipt) {
        return new SalesReceiptResponseDTO(
                receipt.getId(),
                receipt.getSalesOrder().getId(),
                receipt.getReceiptDate(),
                receipt.getAmountReceived()
        );
    }
}
