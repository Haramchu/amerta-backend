package propensi.amesta.model.Aset;

import java.util.Date;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import propensi.amesta.model.EndUser.KepalaGudang;

@Setter
@Getter
@Entity
@Table(name = "gudang")
public class Gudang {

    @Id
    private String nama;

    @NotNull(message = "Deskripsi gudang harus diisi")
    private String deskripsi;

    @ManyToOne
    @JoinColumn(name = "kepala_gudang_id", referencedColumnName = "id")
    private KepalaGudang kepalaGudang;

    @NotNull(message = "Kapasitas gudang harus diisi")
    private int kapasitas;

    @ManyToMany(mappedBy = "gudang")
    private Set<Barang> listBarang;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "alamat_id", referencedColumnName = "id", unique = true)
    private AlamatGudang alamatGudang;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdDate", updatable = false, nullable = false)
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updatedDate", nullable = false)
    private Date updatedDate;
}
