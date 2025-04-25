package propensi.amesta.model.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
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
    private String status; 

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
}
