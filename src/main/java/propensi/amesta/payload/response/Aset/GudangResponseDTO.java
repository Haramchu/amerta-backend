package propensi.amesta.payload.response.Aset;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GudangResponseDTO {
    
    private String nama;
    private String deskripsi;
    private Integer kapasitas;
    private KepalaGudangResponseDTO kepalaGudang;
    private AlamatGudangResponseDTO alamatGudang;
    private List<BarangResponseDTO> listBarang;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date createdDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date updatedDate;
}