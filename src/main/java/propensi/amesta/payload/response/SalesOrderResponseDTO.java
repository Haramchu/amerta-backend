package propensi.amesta.payload.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class SalesOrderResponseDTO {
    private String id;
    private UUID customerId;
    private LocalDate orderDate;
    private String status;
    private BigDecimal totalPrice;
}
