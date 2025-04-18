package propensi.amesta.repository.Sales;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Sales.SalesInvoice;
import propensi.amesta.model.Sales.SalesOrder;

@Repository
public interface SalesInvoiceDb extends JpaRepository<SalesInvoice, String> {
    Optional<SalesInvoice> findBySalesOrder(SalesOrder salesOrder);
    List<SalesInvoice> findByInvoiceDateBetween(LocalDate startDate, LocalDate endDate);
}