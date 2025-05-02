package propensi.amesta.payload.response.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalesInvoiceResponseDTO {
    private String id;
    private String salesOrderId;
    private LocalDate invoiceDate;
    private String invoiceStatus;
    private BigDecimal totalAmount;
    private Integer paymentTerms;
    private LocalDate dueDate;
    private BigDecimal remainingAmount;
}