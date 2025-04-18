package propensi.amesta.repository.Sales;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Sales.SalesOrder;

@Repository
public interface SalesOrderDb extends JpaRepository<SalesOrder, String> {
    List<SalesOrder> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);
    List<SalesOrder> findByStatus(String status);
    List<SalesOrder> findByCustomerId(UUID customerId);
    List<SalesOrder> findByOrderDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, String status);
    List<SalesOrder> findByOrderDateBetweenAndCustomerId(LocalDate startDate, LocalDate endDate, UUID customerId);
    List<SalesOrder> findByStatusAndCustomerId(String status, UUID customerId);
    List<SalesOrder> findByOrderDateBetweenAndStatusAndCustomerId(LocalDate startDate, LocalDate endDate, String status, UUID customerId);
}