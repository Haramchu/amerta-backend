package propensi.amesta.model.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sales_payment")
public class SalesPayment {

    @Id
    private String Id;

    @OneToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

    @NotNull(message = "Tanggal pembayaran tidak boleh kosong")
    private LocalDate paymentDate;

    @NotNull(message = "Metode pembayaran tidak boleh kosong")
    private String paymentMethod;

    @NotNull(message = "Status pembayaran tidak boleh kosong") 
    private String paymentStatus;

    @NotNull(message = "Biaya pembayaran tidak boleh kosong")
    private BigDecimal totalAmountPayed;

}
