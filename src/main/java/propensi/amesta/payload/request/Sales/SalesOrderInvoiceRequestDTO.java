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
}
