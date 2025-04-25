package propensi.amesta.model.Purchase;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.Aset.Gudang;

@Setter
@Getter
@Entity
@Table(name = "purchase_order_item")
public class PurchaseOrderItem {

    @Id
    private UUID Id;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "barang_id")
    private Barang barang; // harga diambil dari sini

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "gudang_nama", referencedColumnName = "nama")
    private Gudang gudangTujuan;

    // private String gudangTujuan;

    // private BigDecimal unitPrice;
    
}
