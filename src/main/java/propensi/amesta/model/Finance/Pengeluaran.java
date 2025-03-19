package propensi.amesta.model.Finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "pengeluaran")
public class Pengeluaran {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "Jenis pengeluaran tidak boleh kosong")
    @Column(name = "jenis_pengeluaran", nullable = false)
    private String jenisPengeluaran;

    @NotNull(message = "Jumlah pengeluaran tidak boleh kosong")
    @Min(value = 0, message = "Jumlah pengeluaran harus positif")
    @Column(name = "jumlah", nullable = false)
    private BigDecimal jumlah;

    @NotNull(message = "Tanggal pengeluaran harus diisi")
    @Column(name = "tanggal", nullable = false)
    private LocalDateTime tanggal;

    @NotBlank(message = "Penanggung jawab tidak boleh kosong")
    @Column(name = "penanggung_jawab", nullable = false)
    private String penanggung_jawab;

    @Column(name = "keterangan")
    private String keterangan;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.tanggal == null) {
            this.tanggal = LocalDateTime.now();
        }
    }
}
