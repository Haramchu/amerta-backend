package propensi.amesta.repository.Purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Purchase.PurchaseInvoice;

@Repository
public interface PurchaseInvoiceDb extends JpaRepository<PurchaseInvoice, String> {
    boolean existsByNomorNota(String nomorNota);
}

