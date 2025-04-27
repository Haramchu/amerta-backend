package propensi.amesta.payload.response.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PurchaseReceiptResponseDTO {
    private String id;
    private String purchaseOrderId;
    private LocalDate receiptDate;
    private BigDecimal amountPayed;

    // tambahin yang lain yang mau ditampilin di nota, jangan lupa di PurchaseReceipt.java
}
