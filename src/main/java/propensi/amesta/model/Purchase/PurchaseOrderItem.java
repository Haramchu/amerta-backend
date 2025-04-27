package propensi.amesta.model.Purchase;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    private Barang barang;

    @NotNull(message = "Kuantitas tidak boleh kosong")
    private Integer quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    
    @Column(name = "subtotal")
    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "gudang_nama", referencedColumnName = "nama")
    private Gudang gudangTujuan;

    @NotNull(message = "Pajak tidak boleh kosong")
    @Min(value = 1, message = "Pajak tidak boleh negatif.")
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Pajak harus merupakan angka yang valid.")
    private Integer tax; // dalam persen 
}