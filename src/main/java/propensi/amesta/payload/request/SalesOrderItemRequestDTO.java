package propensi.amesta.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalesOrderItemRequestDTO {
    @NotEmpty(message = "ID barang tidak boleh kosong")
    private String barangId;
    
    @NotNull(message = "Jumlah barang tidak boleh kosong")
    @Min(value = 1, message = "Jumlah barang minimal 1")
    private Integer quantity;
}