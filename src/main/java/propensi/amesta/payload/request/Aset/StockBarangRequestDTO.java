package propensi.amesta.payload.request.Aset;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockBarangRequestDTO {
        
    @NotNull(message = "Jumlah stok harus diisi.")
    @Min(value = 0, message = "Stok tidak boleh negatif.")
    @Digits(integer = 10, fraction = 0, message = "Stok harus merupakan angka yang valid.")
    private int stock;

    @NotEmpty(message = "Gudang harus diisi")
    private String namaGudang;
        
}
