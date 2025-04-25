package propensi.amesta.model.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "purchase_payment")
public class PurchasePayment {

    @Id
    private String Id;

    @OneToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @NotNull(message = "Tanggal pembayaran tidak boleh kosong")
    private LocalDate paymentDate;

    @NotNull(message = "Metode pembayaran tidak boleh kosong")
    private String paymentMethod;

    @NotNull(message = "Status pembayaran tidak boleh kosong") 
    private String paymentStatus;

    @NotNull(message = "Biaya pembayaran tidak boleh kosong")
    private BigDecimal totalAmountPayed;

}
