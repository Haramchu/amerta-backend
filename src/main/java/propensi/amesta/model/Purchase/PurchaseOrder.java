package propensi.amesta.model.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import propensi.amesta.enums.Purchase.PurchaseOrderStatus;
import propensi.amesta.model.Customer;

@Setter
@Getter
@Entity
@Table(name = "purchase_order")
public class PurchaseOrder {

    @Id
    private String Id;

    @NotNull(message = "Tanggal pembelian tidak boleh kosong")
    private LocalDate purchaseDate;
    
    @NotNull(message = "Status pembelian tidak boleh kosong")
    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus status; 

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private PurchaseInvoice invoice;

    @OneToOne(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private PurchaseReceipt receipt;

    @OneToOne(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private PurchasePayment payment;

    @OneToOne(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private Delivery delivery;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private List<PurchaseOrderItem> items;

    @NotNull(message = "Total harga tidak boleh kosong")
    private BigDecimal totalPrice;
    
    public void setStatusFromString(String statusStr) {
        this.status = PurchaseOrderStatus.fromString(statusStr);
        if (this.status == null) {
            throw new IllegalArgumentException("Status tidak valid: " + statusStr);
        }
    }
    
    public String getStatusAsString() {
        return status != null ? status.getStatus() : null;
    }
}