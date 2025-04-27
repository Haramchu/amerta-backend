package propensi.amesta.model.Sales;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
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