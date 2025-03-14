package propensi.amesta.payload.response;

import java.util.Date;
import java.util.UUID;

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
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date createdDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date updatedDate;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KepalaGudangResponseDTO {
        private UUID id;
        private String name;
        private String username;
        private String email;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlamatGudangResponseDTO {
        private UUID id;
        private String alamat;
        private String kota;
        private String provinsi;
        private String kodePos;
    }
}