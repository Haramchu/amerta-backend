package propensi.amesta.model.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import propensi.amesta.enums.Purchase.InvoiceStatus;

@Setter
@Getter
@Entity
@Table(name = "purchase_invoice")
public class PurchaseInvoice {

    @Id
    private String Id;

    @OneToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @NotNull(message = "Tanggal invoice tidak boleh kosong")
    private LocalDate invoiceDate;

    @NotNull(message = "Status invoice tidak boleh kosong")
    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;

    @NotNull(message = "Biaya invoice tidak boleh kosong")
    private BigDecimal totalAmount; // Amount yang harus dibayar oleh customer, bukan amount yang sudah dibayar

    @NotNull(message = "Payment terms tidak boleh kosong")
    private Integer paymentTerms; // 30 days, 60 days, dll

    @NotNull(message = "Tanggal jatuh tempo tidak boleh kosong")
    private LocalDate dueDate; // untuk PurchaseInvoice

    public void setInvoiceStatusFromString(String statusStr) {
        this.invoiceStatus = InvoiceStatus.fromString(statusStr);
        if (this.invoiceStatus == null) {
            throw new IllegalArgumentException("Status invoice tidak valid: " + statusStr);
        }
    }
    
    public String getInvoiceStatusAsString() {
        return invoiceStatus != null ? invoiceStatus.getStatus() : null;
    }
}