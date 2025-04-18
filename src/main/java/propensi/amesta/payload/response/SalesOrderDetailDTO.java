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
    
    // Invoice details
    private String invoiceId;
    private LocalDate invoiceDate;
    private BigDecimal invoiceAmount;
    
    // Shipping details
    private String shippingId;
    private LocalDate shippingDate;
    private String shippingStatus;
    private String trackingNumber;
    
    // Receipt details
    private String receiptId;
    private LocalDate receiptDate;
    private BigDecimal amountReceived;
}