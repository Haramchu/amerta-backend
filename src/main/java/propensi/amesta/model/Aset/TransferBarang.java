package propensi.amesta.model.Aset;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
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
@Table(name = "transfer_barang")
public class TransferBarang {

    @Id
    private String id;

    @NotNull(message = "Tanggal pemindahan harus diisi")
    @Temporal(TemporalType.DATE)
    private Date tanggalPemindahan;

    @NotNull(message = "Gudang asal harus diisi")
    @ManyToOne
    @JoinColumn(name = "gudang_asal_id", referencedColumnName = "nama")
    private Gudang gudangAsal;

    @NotNull(message = "Gudang tujuan harus diisi")
    @ManyToOne
    @JoinColumn(name = "gudang_tujuan_id", referencedColumnName = "nama")
    private Gudang gudangTujuan;

    @NotNull(message = "Barang yang dipindah harus diisi")
    @ManyToMany(mappedBy = "listTransferBarang", fetch = FetchType.LAZY)
    private List<Barang> listBarang;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdDate", updatable = false, nullable = false)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deletedDate")
    private Date deletedDate;
}
