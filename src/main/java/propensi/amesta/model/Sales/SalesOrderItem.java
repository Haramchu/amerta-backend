package propensi.amesta.model.Sales;

import java.util.UUID;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.Aset.Gudang;

@Setter
@Getter
@Entity
@Table(name = "sales_order_item")
public class SalesOrderItem {

    @Id
    private UUID Id;

    @ManyToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

    @ManyToOne
    @JoinColumn(name = "barang_id")
    private Barang barang;

    @NotNull(message = "Kuantitas tidak boleh kosong")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "gudang_nama", referencedColumnName = "nama")
    private Gudang gudangTujuan;

    @NotNull(message = "Pajak tidak boleh kosong")
    @Min(value = 0, message = "Pajak tidak boleh negatif.")
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Pajak harus merupakan angka yang valid.")
    private Integer tax;
}