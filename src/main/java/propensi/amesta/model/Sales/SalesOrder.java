package propensi.amesta.model.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import propensi.amesta.enums.Sales.SalesOrderStatus;
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
    @Enumerated(EnumType.STRING)
    private SalesOrderStatus status;

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
    
    public void setStatusFromString(String statusStr) {
        this.status = SalesOrderStatus.fromString(statusStr);
        if (this.status == null) {
            throw new IllegalArgumentException("Status tidak valid: " + statusStr);
        }
    }
    
    public String getStatusAsString() {
        return status != null ? status.getStatus() : null;
    }
}