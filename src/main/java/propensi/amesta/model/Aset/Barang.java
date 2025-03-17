package propensi.amesta.model.Aset;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
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
@Table(
    name = "barang",
    uniqueConstraints = @UniqueConstraint(columnNames = {"nama", "merk"})
)
public class Barang {

    @Id
    private String id;

    @NotNull(message = "Nama barang harus diisi")
    private String nama;

    @NotNull(message = "Kategori barang harus diisi")
    private String kategori;

    @NotNull(message = "Kondisi barang harus diisi")
    private boolean isActive;

    @NotNull(message = "Merk barang harus diisi")
    private String merk;

    @OneToMany(mappedBy = "barang", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockBarangPerGudang> listStockBarang;

    @ManyToMany
    @JoinTable(
        name = "transfer_barang_detail",
        joinColumns = @JoinColumn(name = "barang_id"),
        inverseJoinColumns = @JoinColumn(name = "transfer_id")
    )
    private List<TransferBarang> listTransferBarang;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdDate", updatable = false, nullable = false)
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updatedDate", nullable = false)
    private Date updatedDate;
}
