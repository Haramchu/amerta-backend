package propensi.amesta.repository.Purchase;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Purchase.PurchaseOrderItem;

@Repository
public interface PurchaseOrderItemDb extends JpaRepository<PurchaseOrderItem, UUID> {
    boolean existsByBarangIdAndPurchaseOrder_StatusNot(String id, String status);
}
