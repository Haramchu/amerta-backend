package propensi.amesta.model.Sales;

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
@Table(name = "sales_order")
public class SalesOrder {

    @Id
    private String Id;

    @NotNull(message = "Tanggal pembelian tidak boleh kosong")
    private LocalDate purchaseDate;
    
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

}
