package propensi.amesta.payload.request.Sales;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalesOrderInvoiceRequestDTO {
    @NotNull(message = "Tanggal invoice harus diisi")
    private LocalDate invoiceDate;

    @NotNull(message = "Payment Terms tidak boleh kosong")
    @Min(value = 1, message = "Payment Terms minimal 1 hari.")
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Payment Terms harus merupakan angka yang valid.")
    private Integer paymentTerms;
}
