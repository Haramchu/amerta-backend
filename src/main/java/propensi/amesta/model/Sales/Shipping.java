package propensi.amesta.model.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "shipping")
public class Shipping {

    @Id
    private String Id;

    @OneToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

    @NotNull(message = "Tanggal shipping tidak boleh kosong")
    private LocalDate shippingDate;

    @NotNull(message = "Status shipping harus diisi")
    private String shippingStatus;

    @NotNull(message = "Nomor pengiriman tidak boleh kosong")
    private String trackingNumber;

    @NotNull(message = "Biaya pengiriman tidak boleh kosong")
    private BigDecimal shippingFee;
}