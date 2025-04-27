package propensi.amesta.payload.request.Sales;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalesOrderItemRequestDTO {

    @NotEmpty(message = "ID barang tidak boleh kosong")
    private String barangId;

    @NotNull(message = "Quantity tidak boleh kosong")
    private Integer quantity;
}
