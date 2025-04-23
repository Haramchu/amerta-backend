package propensi.amesta.payload.response.Purchase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PurchaseOrderItemResponseDTO {
    private String gudangTujuan;
    private String barangId;
    private Integer quantity;
}
