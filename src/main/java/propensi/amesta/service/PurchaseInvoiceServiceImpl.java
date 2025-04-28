package propensi.amesta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.amesta.model.Purchase.PurchaseInvoice;
import propensi.amesta.model.Purchase.PurchaseOrder;
import propensi.amesta.payload.response.Purchase.PurchaseInvoiceResponseDTO;
import propensi.amesta.repository.Purchase.PurchaseInvoiceDb;
import propensi.amesta.repository.Purchase.PurchaseOrderDb;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseInvoiceServiceImpl implements PurchaseInvoiceService {

    @Autowired
    private PurchaseInvoiceDb purchaseInvoiceDb;

    @Autowired
    private PurchaseOrderDb purchaseOrderDb;

    @Override
    public PurchaseInvoiceResponseDTO getInvoiceById(String id) {
        PurchaseInvoice invoice = purchaseInvoiceDb.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice tidak ditemukan"));

        return invoiceToResponseDTO(invoice);
    }

    @Override
    public List<PurchaseInvoiceResponseDTO> getAllInvoices() {
        List<PurchaseInvoice> invoices = purchaseInvoiceDb.findAll();
        List<PurchaseInvoiceResponseDTO> responseDTOList = new ArrayList<>();
        for (PurchaseInvoice invoice : invoices) {
            responseDTOList.add(invoiceToResponseDTO(invoice));
        }
        return responseDTOList;
    }

    private PurchaseInvoiceResponseDTO invoiceToResponseDTO(PurchaseInvoice invoice) {
        return new PurchaseInvoiceResponseDTO(
                invoice.getId(),
                invoice.getPurchaseOrder().getId(),
                invoice.getInvoiceDate(),
                invoice.getInvoiceStatus(),
                invoice.getTotalAmount(),
                invoice.getPaymentTerms(),
                invoice.getDueDate(),
                invoice.getTotalAmount()
        );
    }
}
