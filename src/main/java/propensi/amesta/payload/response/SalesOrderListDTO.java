package propensi.amesta.payload.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class SalesOrderListDTO {
    private String id;
    private LocalDate orderDate;
    private String status;
    private String customerName;
    private BigDecimal totalPrice;
    private Boolean hasInvoice;
    private Boolean hasShipping;
    private Boolean hasReceipt;
}