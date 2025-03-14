package propensi.amesta.model.Aset;

import java.util.Date;

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

    @NotNull(message = "Nama Barang harus diisi")
    private String namaBarang;

    @NotNull(message = "Alamat gudang harus diisi")
    private String gudangTujuan;

    @NotNull(message = "Alamat gudang harus diisi")
    private String gudangAsal;

    @NotNull(message = "Tipe Proses harus dipilih")
    private int tipeProses; // 0 = Kirim, 1 = Terima

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdDate", updatable = false, nullable = false)
    private Date createdDate;
}
