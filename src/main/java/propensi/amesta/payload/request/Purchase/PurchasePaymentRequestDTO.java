package propensi.amesta.payload.request.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PurchasePaymentRequestDTO {
    
    @NotNull(message = "Tanggal pembayaran tidak boleh kosong")
    private LocalDate paymentDate;

    @NotNull(message = "Metode pembayaran tidak boleh kosong")
    private String paymentMethod;

    @NotNull(message = "Kuantitas barang tidak boleh kosong")
    @Min(value = 1, message = "Kuantitas barang tidak boleh negatif.")
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Kuantitas barang harus merupakan angka yang valid.")
    private BigDecimal totalAmountPayed;

}
