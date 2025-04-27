package propensi.amesta.payload.response;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BarangResponseDTO {
    private String id;
    private String nama;
    private String kategori;
    private boolean isActive;
    private String merk;
    private List<StockBarangResponseDTO> stockBarang;
    private int totalStock;
    private BigDecimal hargaBeli;
    private BigDecimal hargaJual;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy | HH:mm:ss", timezone = "Asia/Jakarta")
    private Date createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy | HH:mm:ss", timezone = "Asia/Jakarta")
    private Date updatedDate;
}
