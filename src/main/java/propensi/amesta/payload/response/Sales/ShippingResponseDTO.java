package propensi.amesta.payload.response.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShippingResponseDTO {
    private String id;
    private String salesOrderId;
    private LocalDate deliveryDate;
    private String deliveryStatus;
    private String trackingNumber;
    private BigDecimal deliveryFee;
}