package propensi.amesta.payload.request.Purchase;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PurchaseOrderItemRequestDTO {
    @NotEmpty(message = "ID barang tidak boleh kosong")
    private String barangId;

    @NotNull(message = "Kuantitas barang tidak boleh kosong")
    @Min(value = 1, message = "Kuantitas barang tidak boleh negatif.")
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Kuantitas barang harus merupakan angka yang valid.")
    private Integer quantity;

    @NotEmpty(message = "Gudang tujuan tidak boleh kosong")
    private String gudangTujuan;

    @NotNull(message = "Pajak tidak boleh kosong")
    @Min(value = 0, message = "Pajak tidak boleh negatif.")
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Pajak harus merupakan angka yang valid.")
    private Integer pajak;

}
