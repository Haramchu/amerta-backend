package propensi.amesta.payload.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GudangListResponseDTO {
    
    private String nama;
    private String deskripsi;
    private Integer kapasitas;
    private String kotaGudang;
    private String provinsiGudang;
    private String namaKepalaGudang;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date createdDate;
}