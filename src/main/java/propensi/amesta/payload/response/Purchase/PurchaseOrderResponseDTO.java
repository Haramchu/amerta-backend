package propensi.amesta.payload.response.Purchase;

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

    // Invoice (Faktur), untuk Ricky
    private PurchaseInvoiceResponseDTO invoice;

    // Delivery (Pengiriman), untuk Jess
    private DeliveryResponseDTO delivery;

    private PurchasePaymentResponseDTO payment;
    private String status;
    private List<PurchaseOrderItemResponseDTO> items;

    // Receipt (Nota), untuk Michael
    private PurchaseReceiptResponseDTO receipt;
}
