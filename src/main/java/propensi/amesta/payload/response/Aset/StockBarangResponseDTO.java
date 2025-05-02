package propensi.amesta.payload.response.Aset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StockBarangResponseDTO {
    private Integer stock;
    private String idBarang;
    private String namaGudang;
}
