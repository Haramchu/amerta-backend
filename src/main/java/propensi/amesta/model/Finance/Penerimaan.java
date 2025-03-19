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
@Table(name = "penerimaan")
public class Penerimaan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "Jenis penerimaan tidak boleh kosong")
    @Column(name = "jenis_penerimaan", nullable = false)
    private String jenisPenerimaan;

    @NotNull(message = "Jumlah penerimaan tidak boleh kosong")
    @Min(value = 0, message = "Jumlah penerimaan harus positif")
    @Column(name = "jumlah", nullable = false)
    private BigDecimal jumlah;

    @NotNull(message = "Tanggal penerimaan harus diisi")
    @Column(name = "tanggal", nullable = false)
    private LocalDateTime tanggal;

    @NotBlank(message = "Sumber penerimaan tidak boleh kosong")
    @Column(name = "sumber_penerimaan", nullable = false)
    private String sumberPenerimaan;

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
