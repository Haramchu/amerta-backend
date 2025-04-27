package propensi.amesta.payload.request.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalesPaymentRequestDTO {
    
    @NotNull(message = "Tanggal pembayaran tidak boleh kosong")
    private LocalDate paymentDate;

    @NotNull(message = "Metode pembayaran tidak boleh kosong")
    private String paymentMethod;

    @NotNull(message = "Pembayaran tidak boleh kosong")
    @DecimalMin(value = "1.0", message = "Pembayaran minimal 1.")
    private BigDecimal totalAmountPayed;

}
