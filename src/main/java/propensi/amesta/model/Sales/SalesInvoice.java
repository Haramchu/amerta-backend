package propensi.amesta.model.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sales_invoice")
public class SalesInvoice {

    @Id
    private String Id;

    @OneToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

    @NotNull(message = "Tanggal invoice tidak boleh kosong")
    private LocalDate invoiceDate;

    @NotNull(message = "Status invoice tidak boleh kosong")
    private String invoiceStatus;

    @NotNull(message = "Biaya invoice tidak boleh kosong")
    private BigDecimal totalAmount;

}
