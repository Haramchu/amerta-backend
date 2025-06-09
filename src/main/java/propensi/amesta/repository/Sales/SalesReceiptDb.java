package propensi.amesta.repository.Sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Sales.SalesReceipt;

@Repository
public interface SalesReceiptDb extends JpaRepository<SalesReceipt, String> {

}
