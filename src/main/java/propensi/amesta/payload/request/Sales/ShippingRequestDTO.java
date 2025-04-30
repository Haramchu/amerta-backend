package propensi.amesta.payload.request.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShippingRequestDTO {
    @NotNull(message = "Tanggal pengiriman tidak boleh kosong")
    private LocalDate shippingDate;

    @NotNull(message = "Biaya pengiriman tidak boleh kosong")
    @DecimalMin(value = "0", message = "Biaya pengiriman minimal 0") // boleh gratis ongkir
    private BigDecimal shippingFee;

}
