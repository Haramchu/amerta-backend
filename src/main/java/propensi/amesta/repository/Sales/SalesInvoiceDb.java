package propensi.amesta.repository.Sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import propensi.amesta.model.Sales.SalesInvoice;

@Repository
public interface SalesInvoiceDb extends JpaRepository<SalesInvoice, String> {
    
}