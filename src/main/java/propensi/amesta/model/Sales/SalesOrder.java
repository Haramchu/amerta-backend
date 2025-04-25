package propensi.amesta.model.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
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

    @Column(name = "order_date")
    private LocalDate orderDate;
    
    @Column(name = "status")
    private String status; // CREATED, INVOICED, SHIPPED, COMPLETED, CANCELLED

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private List<SalesOrderItem> items;

    @OneToOne(mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private SalesInvoice invoice;

    @OneToOne(mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private Shipping shipping;

    @OneToOne(mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private SalesReceipt receipt;

    @Column(name = "total_price")
    private BigDecimal totalPrice;
}