package propensi.amesta.repository.Sales;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import propensi.amesta.model.Sales.SalesInvoice;

@Repository
public interface SalesInvoiceDb extends JpaRepository<SalesInvoice, String> {
    @Query("SELECT FUNCTION('TO_CHAR', s.invoiceDate, 'YYYY-MM') AS bulan, SUM(s.totalAmount) " +
            "FROM SalesInvoice s " +
            "WHERE s.invoiceDate BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('TO_CHAR', s.invoiceDate, 'YYYY-MM') " +
            "ORDER BY bulan")
    List<Object[]> aggregateTotalAmountPerMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);

}