package propensi.amesta.payload.response.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class SalesOrderResponseDTO {
    private String id;
    private UUID customerId;
    private LocalDate orderDate;
    private String status;
    private BigDecimal totalPrice;
}
