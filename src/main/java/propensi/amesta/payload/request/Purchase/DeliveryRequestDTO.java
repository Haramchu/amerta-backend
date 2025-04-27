package propensi.amesta.payload.request.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeliveryRequestDTO {
    @NotNull(message = "Tanggal pengiriman tidak boleh kosong")
    private LocalDate deliveryDate;

    @NotNull(message = "Biaya pengiriman tidak boleh kosong")
    @DecimalMin(value = "0", message = "Biaya pengiriman minimal 0") // boleh gratis ongkir
    private BigDecimal deliveryFee;

}
