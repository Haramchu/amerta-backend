package propensi.amesta.payload.response.Aset;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import propensi.amesta.payload.request.Aset.BarangTransferDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferBarangResponseDTO {
    
    private String id;
    private Date tanggalPemindahan;
    private String gudangAsal;
    private String gudangTujuan;
    private List<BarangTransferDTO> listBarang;
    private Date createdDate;
}
