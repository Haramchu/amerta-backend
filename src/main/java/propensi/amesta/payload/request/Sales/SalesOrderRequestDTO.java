package propensi.amesta.payload.request.Sales;

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
public class SalesOrderRequestDTO {
    
    @NotNull(message = "Tanggal pembelian harus diisi")
    private LocalDate salesDate;

    @NotNull(message = "Customer ID tidak boleh kosong")
    private UUID customerId;

    @NotNull(message = "List barang tidak boleh kosong")
    private List<SalesOrderItemRequestDTO> items;

    @AssertTrue(message = "Kuantitas barang tidak boleh negatif")
    private boolean isQuantityValid() {
        for (SalesOrderItemRequestDTO item : items) {
            if (item.getQuantity() < 1) {
                return false;
            }
        }
        return true;
    }

}
