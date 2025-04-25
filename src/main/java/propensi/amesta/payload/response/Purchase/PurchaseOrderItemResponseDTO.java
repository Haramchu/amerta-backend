package propensi.amesta.payload.response.Purchase;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PurchaseOrderItemResponseDTO {
    private UUID id;
    private String purchaseOrderId;
    private String barangId;
    private Integer quantity;
    private String gudangTujuan;
}
