package propensi.amesta.payload.response.Purchase;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PurchasePaymentResponseDTO {
    private LocalDate paymentDate;
    private String paymentMethod;
}
