package propensi.amesta.repository.Purchase;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Purchase.PurchaseInvoice;

@Repository
public interface PurchaseInvoiceDb extends JpaRepository<PurchaseInvoice, String> {
    @Query("SELECT FUNCTION('TO_CHAR', p.invoiceDate, 'YYYY-MM') AS bulan, SUM(p.totalAmount) " +
            "FROM PurchaseInvoice p " +
            "WHERE p.invoiceDate BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('TO_CHAR', p.invoiceDate, 'YYYY-MM') " +
            "ORDER BY bulan")
    List<Object[]> aggregateTotalAmountPerMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);

}
