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
    private LocalDate invoiceDate;

    @NotNull(message = "Payment Terms tidak boleh kosong")
    @Min(value = 1, message = "Payment Terms tidak boleh negatif.")
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Payment Terms harus merupakan angka yang valid.")
    private Integer paymentTerms;
}
