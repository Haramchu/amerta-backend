package propensi.amesta.payload.response;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferBarangResponseDTO {
    
    private String id;
    private Date tanggalPemindahan;
    private String gudangAsal;
    private String gudangTujuan;
    private List<String> listBarang;
    private Date createdDate;
}
