package propensi.amesta.repository.Purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Purchase.PurchaseReceipt;

@Repository
public interface PurchaseReceiptDb extends JpaRepository<PurchaseReceipt, String> {

}
