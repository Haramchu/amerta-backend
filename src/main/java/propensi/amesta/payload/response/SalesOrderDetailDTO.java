package propensi.amesta.payload.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class SalesOrderDetailDTO {
    private String id;
    private LocalDate orderDate;
    private String status;
    private String customerId;
    private String customerName;
    private BigDecimal totalPrice;
    private List<SalesOrderItemDTO> items;
}