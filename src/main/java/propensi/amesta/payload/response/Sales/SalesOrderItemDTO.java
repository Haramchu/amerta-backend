package propensi.amesta.payload.response.Sales;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SalesOrderItemDTO {
    private String id;
    private String barangId;
    private String barangName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}