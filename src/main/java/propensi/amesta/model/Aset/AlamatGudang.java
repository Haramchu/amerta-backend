package propensi.amesta.model.Aset;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

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
@Table(name = "alamat_gudang")
public class AlamatGudang {

    @Id
    private UUID id = UUID.randomUUID();

    @NotNull(message = "Alamat gudang harus diisi")
    private String alamat;

    @NotNull(message = "Kota gudang harus diisi")
    private String kota;

    @NotNull(message = "Provinsi gudang harus diisi")
    private String provinsi;

    @NotNull(message = "Kode Pos gudang harus diisi")
    private String kodePos;

    @OneToOne(mappedBy = "alamatGudang", cascade = CascadeType.ALL, orphanRemoval = true)
    private Gudang gudang;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdDate", updatable = false, nullable = false)
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updatedDate", nullable = false)
    private Date updatedDate;
    
}
