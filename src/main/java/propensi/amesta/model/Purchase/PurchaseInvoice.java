package propensi.amesta.model.Purchase;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "purchase_invoice")
public class PurchaseInvoice {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "nomor_nota", nullable = false)
    private String nomorNota;

    @Column(name = "tanggal_dibuat", nullable = false)
    private LocalDateTime tanggalDibuat;

    @Column(name = "jumlah_tagihan", nullable = false)
    private BigDecimal jumlahTagihan;

    @Column(name = "status_pembayaran", nullable = false)
    private String statusPembayaran;

    @PrePersist
    protected void onCreate() {
        if (this.tanggalDibuat == null) {
            this.tanggalDibuat = LocalDateTime.now();
        }
    }
}
