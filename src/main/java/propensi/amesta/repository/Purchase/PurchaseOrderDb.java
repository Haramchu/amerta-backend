package propensi.amesta.repository.Purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Purchase.PurchaseOrder;

@Repository
public interface PurchaseOrderDb extends JpaRepository<PurchaseOrder, String> {

}
