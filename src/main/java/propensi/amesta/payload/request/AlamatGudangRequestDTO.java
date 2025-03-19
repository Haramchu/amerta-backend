package propensi.amesta.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlamatGudangRequestDTO {
    
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