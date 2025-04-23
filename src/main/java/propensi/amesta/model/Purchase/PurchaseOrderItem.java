package propensi.amesta.model.Purchase;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import propensi.amesta.model.Aset.Barang;

@Setter
@Getter
@Entity
@Table(name = "purchase_order_item")
public class PurchaseOrderItem {

    @Id
    private String Id;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "barang_id")
    private Barang barang;

    private Integer quantity;

    private BigDecimal unitPrice;
    
}
