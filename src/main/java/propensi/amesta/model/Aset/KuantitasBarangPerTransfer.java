package propensi.amesta.model.Aset;

import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kuantitas_barang_per_transfer")
public class KuantitasBarangPerTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "transfer_id", referencedColumnName = "id", nullable = false)
    private TransferBarang transferBarang;

    @ManyToOne
    @JoinColumn(name = "barang_id", referencedColumnName = "id", nullable = false)
    private Barang barang;

    @NotNull(message = "Jumlah barang harus diisi")
    @Min(value = 1, message = "Jumlah barang minimal 1")
    private Integer jumlah;
}
