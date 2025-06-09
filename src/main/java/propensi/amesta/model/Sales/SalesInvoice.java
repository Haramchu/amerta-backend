package propensi.amesta.model.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sales_invoice")
public class SalesInvoice {

    @Id
    private String id;

    @OneToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

    @NotNull(message = "Tanggal invoice tidak boleh kosong")
    private LocalDate invoiceDate;

    @NotNull(message = "Status invoice tidak boleh kosong")
    private String invoiceStatus;

    @NotNull(message = "Biaya invoice tidak boleh kosong")
    private BigDecimal totalAmount; // Amount yang harus dibayar oleh customer, bukan amount yang sudah dibayar

    @NotNull(message = "Payment terms tidak boleh kosong")
    private Integer paymentTerms; // 30 days, 60 days, dll

    @NotNull(message = "Tanggal jatuh tempo tidak boleh kosong")
    private LocalDate dueDate; // untuk SalesInvoice
}