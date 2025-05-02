package propensi.amesta.model.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import propensi.amesta.model.Customer;

@Setter
@Getter
@Entity
@Table(name = "sales_order")
public class SalesOrder {

    @Id
    private String id;

    @NotNull(message = "Tanggal pembelian tidak boleh kosong")
    private LocalDate salesDate;
    
    @NotNull(message = "Status pembelian tidak boleh kosong")
    private String status; 

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne(mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private SalesInvoice invoice;

    @OneToOne(mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private SalesReceipt receipt;

    @OneToOne(mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private SalesPayment payment;

    @OneToOne(mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private Shipping shipping;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private List<SalesOrderItem> items;

    @NotNull(message = "Total harga tidak boleh kosong")
    @Column(name = "total_price")
    private BigDecimal totalPrice;
}