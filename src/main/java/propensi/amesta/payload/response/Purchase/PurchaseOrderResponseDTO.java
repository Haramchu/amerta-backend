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
    private String status;
    private List<PurchaseOrderItemResponseDTO> items;
    private BigDecimal totalPrice;

    // Invoice (Faktur), untuk Ricky
    private PurchaseInvoiceResponseDTO invoice;

    // Delivery (Surat Jalan), untuk Jess
    private DeliveryResponseDTO delivery;

    // Receipt (Nota), untuk Michael
    private PurchaseReceiptResponseDTO receipt;

    private PurchasePaymentResponseDTO payment; 
}