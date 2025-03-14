package propensi.amesta.payload.request;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    
    private UUID kepalaGudangId;
    
    @NotEmpty(message = "Alamat gudang harus diisi")
    private String alamat;
    
    @NotEmpty(message = "Kota gudang harus diisi")
    private String kota;
    
    @NotEmpty(message = "Provinsi gudang harus diisi")
    private String provinsi;
    
    @NotEmpty(message = "Kode pos gudang harus diisi")
    @Pattern(regexp = "\\d{5}", message = "Kode pos harus terdiri dari 5 digit angka")
    private String kodePos;
}