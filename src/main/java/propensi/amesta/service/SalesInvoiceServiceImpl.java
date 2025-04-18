package propensi.amesta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.amesta.model.Sales.SalesInvoice;
import propensi.amesta.repository.Sales.SalesInvoiceDb;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class SalesInvoiceServiceImpl implements SalesInvoiceService {

    @Autowired
    private SalesInvoiceDb salesInvoiceDb;

    @Override
    public SalesInvoice generateInvoice(String nomorNota, String id, String status, BigDecimal total) {
        SalesInvoice invoice = new SalesInvoice();
        invoice.setId(id);
        invoice.setNomorNota(nomorNota);
        invoice.setJumlahTagihan(total);
        invoice.setStatusPembayaran(status);
        invoice.setTanggalDibuat(LocalDateTime.now());
        return salesInvoiceDb.save(invoice);
    }
}
