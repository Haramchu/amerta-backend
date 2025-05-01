package propensi.amesta.repository.Shipping;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Shipping.ShippingDocument;

@Repository
public interface ShippingDocumentDb extends JpaRepository<ShippingDocument, String> {
    List<ShippingDocument> findByStatus(String status);
    List<ShippingDocument> findByDocumentDateBetween(LocalDate startDate, LocalDate endDate);
    Optional<ShippingDocument> findByPurchaseOrderId(String purchaseOrderId);
    Optional<ShippingDocument> findBySalesOrderId(String salesOrderId);
    
    @Query("SELECT sd FROM ShippingDocument sd WHERE sd.customer.id = :customerId")
    List<ShippingDocument> findByCustomerId(@Param("customerId") UUID customerId);
    
    @Query("SELECT sd FROM ShippingDocument sd WHERE " +
           "(:status IS NULL OR sd.status = :status) AND " +
           "(:startDate IS NULL OR sd.documentDate >= :startDate) AND " +
           "(:endDate IS NULL OR sd.documentDate <= :endDate) AND " +
           "(:customerId IS NULL OR sd.customer.id = :customerId)")
    List<ShippingDocument> findByFilters(
        @Param("status") String status,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("customerId") UUID customerId
    );
}