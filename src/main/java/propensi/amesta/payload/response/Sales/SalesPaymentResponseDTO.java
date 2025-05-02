package propensi.amesta.payload.response.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalesPaymentResponseDTO {
    private String id;
    private String salesOrderId;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal totalAmountPayed;
}