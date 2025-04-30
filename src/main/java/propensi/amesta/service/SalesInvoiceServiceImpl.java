package propensi.amesta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.amesta.model.Sales.SalesInvoice;
import propensi.amesta.model.Sales.SalesOrder;
import propensi.amesta.payload.response.Sales.SalesInvoiceResponseDTO;
import propensi.amesta.repository.Sales.SalesInvoiceDb;
import propensi.amesta.repository.Sales.SalesOrderDb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SalesInvoiceServiceImpl implements SalesInvoiceService {

    @Autowired
    private SalesInvoiceDb salesInvoiceDb;

    @Autowired
    private SalesOrderDb salesOrderDb;

    @Override
    public SalesInvoiceResponseDTO getInvoiceById(String id) {
        SalesInvoice invoice = salesInvoiceDb.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice tidak ditemukan"));

        return toDTO(invoice);
    }

    @Override
    public List<SalesInvoiceResponseDTO> getAllInvoices() {
        List<SalesInvoice> invoices = salesInvoiceDb.findAll();
        List<SalesInvoiceResponseDTO> result = new ArrayList<>();
        for (SalesInvoice invoice : invoices) {
            result.add(toDTO(invoice));
        }
        return result;
    }

    private SalesInvoiceResponseDTO toDTO(SalesInvoice salesInvoice) {
        BigDecimal remainingAmount = salesInvoice.getTotalAmount();

        if (salesInvoice.getSalesOrder().getPayment() != null) {
            remainingAmount = salesInvoice.getTotalAmount()
                    .subtract(salesInvoice.getSalesOrder().getPayment().getTotalAmountPayed());
        } else {
            remainingAmount = salesInvoice.getTotalAmount();
        }

        return new SalesInvoiceResponseDTO(
                salesInvoice.getId(),
                salesInvoice.getSalesOrder().getId(),
                salesInvoice.getInvoiceDate(),
                salesInvoice.getInvoiceStatus(),
                salesInvoice.getTotalAmount(),
                salesInvoice.getPaymentTerms(),
                salesInvoice.getDueDate(),
                remainingAmount);
    }
}
