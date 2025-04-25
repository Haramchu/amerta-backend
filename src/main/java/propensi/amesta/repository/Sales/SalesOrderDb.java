package propensi.amesta.repository.Sales;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Sales.SalesOrder;

@Repository
public interface SalesOrderDb extends JpaRepository<SalesOrder, String> {
    List<SalesOrder> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);
    List<SalesOrder> findByStatus(String status);
    
    @Query("SELECT so FROM SalesOrder so WHERE so.customer.id = :customerId")
    List<SalesOrder> findByCustomerId(@Param("customerId") UUID customerId);
    
    @Query("SELECT so FROM SalesOrder so WHERE so.orderDate BETWEEN :startDate AND :endDate AND so.status = :status")
    List<SalesOrder> findByOrderDateBetweenAndStatus(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate, 
        @Param("status") String status);
    
    @Query("SELECT so FROM SalesOrder so WHERE so.orderDate BETWEEN :startDate AND :endDate AND so.customer.id = :customerId")
    List<SalesOrder> findByOrderDateBetweenAndCustomerId(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate, 
        @Param("customerId") UUID customerId);
    
    @Query("SELECT so FROM SalesOrder so WHERE so.status = :status AND so.customer.id = :customerId")
    List<SalesOrder> findByStatusAndCustomerId(
        @Param("status") String status, 
        @Param("customerId") UUID customerId);
    
    @Query("SELECT so FROM SalesOrder so WHERE so.orderDate BETWEEN :startDate AND :endDate AND so.status = :status AND so.customer.id = :customerId")
    List<SalesOrder> findByOrderDateBetweenAndStatusAndCustomerId(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate, 
        @Param("status") String status, 
        @Param("customerId") UUID customerId);
}