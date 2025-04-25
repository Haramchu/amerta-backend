package propensi.amesta.payload.response.Aset;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlamatGudangResponseDTO {
    private UUID id;
    private String alamat;
    private String kota;
    private String provinsi;
    private String kodePos;
}