package propensi.amesta.payload.request;

import java.util.Date;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferBarangRequestDTO {

    @NotNull(message = "Tanggal pemindahan harus diisi")
    private Date tanggalPemindahan;

    @NotNull(message = "Gudang asal harus diisi")
    private String gudangAsal;

    @NotNull(message = "Gudang tujuan harus diisi")
    private String gudangTujuan;

    @NotNull(message = "Barang yang dipindah harus diisi")
    @Valid
    private List<BarangTransferDTO> listBarang;
}
