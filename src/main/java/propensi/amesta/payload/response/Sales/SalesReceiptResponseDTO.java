package propensi.amesta.payload.response.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalesReceiptResponseDTO {
    private String id;
    private String salesOrderId;
    private LocalDate receiptDate;
    private BigDecimal amountPayed;

    // tambahin yang lain yang mau ditampilin di nota, jangan lupa di SalesReceipt.java
}
