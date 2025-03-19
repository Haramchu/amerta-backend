package propensi.amesta.model.Aset;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "stock_barang_per_gudang")
public class StockBarangPerGudang {
    
    @Id
    private UUID id = UUID.randomUUID();
    
    @NotNull(message = "Jumlah barang harus diisi")
    private int stock;

    @ManyToOne
    @JoinColumn(name = "id_barang", referencedColumnName = "id")
    private Barang barang;

    @ManyToOne
    @JoinColumn(name = "nama_gudang", referencedColumnName = "nama")
    private Gudang gudang;
}
