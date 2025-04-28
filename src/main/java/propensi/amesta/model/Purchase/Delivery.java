package propensi.amesta.model.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "delivery")
public class Delivery {

    @Id
    private String Id;

    @OneToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @NotNull(message = "Tanggal delivery tidak boleh kosong")
    private LocalDate deliveryDate;

    @NotNull(message = "Status delivery harus diisi")
    private String deliveryStatus;

    @NotNull(message = "Nomor pengiriman tidak boleh kosong")
    private String trackingNumber;

    @NotNull(message = "Biaya pengiriman tidak boleh kosong")
    private BigDecimal deliveryFee;

}
