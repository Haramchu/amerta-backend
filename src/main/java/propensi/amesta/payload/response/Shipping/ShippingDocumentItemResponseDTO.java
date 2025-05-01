package propensi.amesta.payload.response.Shipping;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDocumentItemResponseDTO {
    private UUID id;
    private String shippingDocumentId;
    private String barangId;
    private String barangNama;
    private String barangMerk;
    private Integer quantity;
    private String gudangNama;
}