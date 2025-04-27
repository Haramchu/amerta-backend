package propensi.amesta.payload.request.Purchase;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PurchaseOrderRequestDTO {
    
    @NotNull(message = "Tanggal pembelian harus diisi")
    private LocalDate purchaseDate;

    @NotNull(message = "Customer ID tidak boleh kosong")
    private UUID customerId;

    @NotNull(message = "List barang tidak boleh kosong")
    private List<PurchaseOrderItemRequestDTO> items;

    @AssertTrue(message = "Kuantitas barang tidak boleh negatif")
    private boolean isQuantityValid() {
        for (PurchaseOrderItemRequestDTO item : items) {
            if (item.getQuantity() < 1) {
                return false;
            }
        }
        return true;
    }

}
