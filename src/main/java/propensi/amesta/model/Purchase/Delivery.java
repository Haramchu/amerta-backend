package propensi.amesta.model.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import propensi.amesta.enums.Purchase.DeliveryStatus;

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
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @NotNull(message = "Nomor pengiriman tidak boleh kosong")
    private String trackingNumber;

    @NotNull(message = "Biaya pengiriman tidak boleh kosong")
    private BigDecimal deliveryFee;

    public void setDeliveryStatusFromString(String statusStr) {
        this.deliveryStatus = DeliveryStatus.fromString(statusStr);
        if (this.deliveryStatus == null) {
            throw new IllegalArgumentException("Status pengiriman tidak valid: " + statusStr);
        }
    }

    public String getDeliveryStatusAsString() {
        return deliveryStatus != null ? deliveryStatus.getStatus() : null;
    }
}