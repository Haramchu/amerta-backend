package propensi.amesta.payload.response.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeliveryResponseDTO {
    private String id;
    private String purchaseOrderId;
    private LocalDate deliveryDate;
    private String deliveryStatus;
    private String trackingNumber;
    private BigDecimal deliveryFee;
}