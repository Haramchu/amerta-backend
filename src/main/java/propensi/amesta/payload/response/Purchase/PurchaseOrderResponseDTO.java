package propensi.amesta.payload.response.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PurchaseOrderResponseDTO {
    private String id;
    private UUID customerId;
    private LocalDate purchaseDate;
    private PurchaseInvoiceResponseDTO invoice;
    private DeliveryResponseDTO delivery;
    private PurchasePaymentResponseDTO payment;
    private String status;
    private List<PurchaseOrderItemResponseDTO> items;
}
