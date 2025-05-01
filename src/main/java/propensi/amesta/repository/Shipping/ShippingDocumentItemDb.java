package propensi.amesta.repository.Shipping;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Shipping.ShippingDocument;
import propensi.amesta.model.Shipping.ShippingDocumentItem;

@Repository
public interface ShippingDocumentItemDb extends JpaRepository<ShippingDocumentItem, UUID> {
    List<ShippingDocumentItem> findByShippingDocument(ShippingDocument shippingDocument);
    List<ShippingDocumentItem> findByShippingDocumentId(String shippingDocumentId);
    void deleteByShippingDocumentId(String shippingDocumentId);
}