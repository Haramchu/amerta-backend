package propensi.amesta.payload.response.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PurchaseInvoiceResponseDTO {
    private String id;
    private String purchaseOrderId;
    private LocalDate invoiceDate;
    private String invoiceStatus;
    private BigDecimal totalAmount;
    private Integer paymentTerms;
    private LocalDate dueDate;
    private BigDecimal remainingAmount;
}