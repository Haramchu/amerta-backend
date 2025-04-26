package propensi.amesta.payload.request.Purchase;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PurchaseOrderInvoiceRequestDTO {
    @NotNull(message = "Tanggal invoice harus diisi")
    private LocalDate invoiceDate; // untuk PurchaseInvoice
}
