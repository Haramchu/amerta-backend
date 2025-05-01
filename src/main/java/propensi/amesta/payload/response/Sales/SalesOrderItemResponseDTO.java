package propensi.amesta.payload.response.Sales;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalesOrderItemResponseDTO {
    private UUID id;
    private String salesOrderId;
    private String barangId;
    private Integer quantity;
    private String gudangTujuan;
    private Integer tax;
}
