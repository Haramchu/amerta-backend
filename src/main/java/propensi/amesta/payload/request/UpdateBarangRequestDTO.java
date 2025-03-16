package propensi.amesta.payload.request;

import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateBarangRequestDTO {
    @NotEmpty(message = "Nama barang harus diisi")
    private String nama;

    @NotEmpty(message = "Kategori barang harus diisi.")
    private String kategori;

    @NotNull(message = "Kondisi barang harus diisi.")
    private boolean isActive;

    @NotEmpty(message = "Merk barang harus diisi.")
    private String merk;

    @NotNull(message= "List stok barang harus diisi")
    @Size(min = 1, message = "Harus ada minimal satu stok barang")
    private List<StockBarangRequestDTO> listStockBarang;

    @AssertTrue(message = "Stok barang harus merupakan angka yang valid")
    private boolean isQuantityValid() {
        for (StockBarangRequestDTO barang : listStockBarang) {
            if (barang.getStock() < 1) {
                return false;
            }
        }
        return true;
    }
}
