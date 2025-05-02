package propensi.amesta.repository.Sales;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Sales.SalesOrderItem;

@Repository
public interface SalesOrderItemDb extends JpaRepository<SalesOrderItem, UUID> {
    
}
