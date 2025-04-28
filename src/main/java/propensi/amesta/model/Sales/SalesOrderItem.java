package propensi.amesta.model.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import propensi.amesta.model.Customer;
import propensi.amesta.model.Aset.Barang;

@Setter
@Getter
@Entity
@Table(name = "sales_order_item")
public class SalesOrderItem {

    @Id
    private String Id;

     @ManyToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

    @ManyToOne
    @JoinColumn(name = "barang_id")
    private Barang barang;

    private Integer quantity;
    
    private BigDecimal unitPrice;
}
