package propensi.amesta.repository.Sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Sales.SalesPayment;

@Repository
public interface SalesPaymentDb extends JpaRepository<SalesPayment, String> {

}
