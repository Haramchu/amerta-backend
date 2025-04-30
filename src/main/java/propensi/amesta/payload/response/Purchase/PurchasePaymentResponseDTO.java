package propensi.amesta.payload.response.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PurchasePaymentResponseDTO {
    private String id;
    private String purchaseOrderId;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal totalAmountPayed;
}
