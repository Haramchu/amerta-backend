package propensi.amesta.payload.request.Shipping;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDocumentItemRequestDTO {
    
    @NotEmpty(message = "ID barang tidak boleh kosong")
    private String barangId;
    
    @NotNull(message = "Kuantitas tidak boleh kosong")
    @Min(value = 1, message = "Kuantitas minimal 1")
    private Integer quantity;
    
    @NotEmpty(message = "Nama gudang tidak boleh kosong")
    private String gudangNama;
}