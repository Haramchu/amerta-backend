package propensi.amesta.payload.request;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GudangRequestDTO {
    
    @NotEmpty(message = "Nama gudang harus diisi")
    private String nama;
    
    @NotEmpty(message = "Deskripsi gudang harus diisi")
    private String deskripsi;
    
    @NotNull(message = "Kapasitas gudang harus diisi")
    @Min(value = 1, message = "Kapasitas gudang minimal 1")
    private Integer kapasitas;

    private AlamatGudangRequestDTO alamatGudang;

    private UUID kepalaGudangId;
}