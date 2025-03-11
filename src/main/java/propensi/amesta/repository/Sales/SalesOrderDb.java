package propensi.amesta.repository.Sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Sales.SalesOrder;

@Repository
public interface SalesOrderDb extends JpaRepository<SalesOrder, String> {

}
