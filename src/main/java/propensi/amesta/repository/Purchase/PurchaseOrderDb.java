package propensi.amesta.repository.Purchase;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Purchase.PurchaseOrder;

@Repository
public interface PurchaseOrderDb extends JpaRepository<PurchaseOrder, String> {
    List<PurchaseOrder> findByPurchaseDateBetween(LocalDate startDate, LocalDate endDate);
    List<PurchaseOrder> findByStatus(String status);
    
    @Query("SELECT po FROM PurchaseOrder po WHERE po.customer.id = :customerId")
    List<PurchaseOrder> findByCustomerId(@Param("customerId") UUID customerId);
    
    @Query("SELECT po FROM PurchaseOrder po WHERE po.purchaseDate BETWEEN :startDate AND :endDate AND po.status = :status")
    List<PurchaseOrder> findByPurchaseDateBetweenAndStatus(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate, 
        @Param("status") String status);
    
    @Query("SELECT po FROM PurchaseOrder po WHERE po.purchaseDate BETWEEN :startDate AND :endDate AND po.customer.id = :customerId")
    List<PurchaseOrder> findByPurchaseDateBetweenAndCustomerId(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate, 
        @Param("customerId") UUID customerId);
    
    @Query("SELECT po FROM PurchaseOrder po WHERE po.status = :status AND po.customer.id = :customerId")
    List<PurchaseOrder> findByStatusAndCustomerId(
        @Param("status") String status, 
        @Param("customerId") UUID customerId);
    
    @Query("SELECT po FROM PurchaseOrder po WHERE po.purchaseDate BETWEEN :startDate AND :endDate AND po.status = :status AND po.customer.id = :customerId")
    List<PurchaseOrder> findByPurchaseDateBetweenAndStatusAndCustomerId(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate, 
        @Param("status") String status, 
        @Param("customerId") UUID customerId);
}