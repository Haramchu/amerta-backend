package propensi.amesta.payload.request.Aset;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarangTransferDTO {

    @NotEmpty(message = "ID barang harus diisi")
    private String id;

    @NotNull(message = "Jumlah barang harus diisi")
    @Min(value = 1, message = "Jumlah barang minimal 1")
    private Integer jumlah;
}
