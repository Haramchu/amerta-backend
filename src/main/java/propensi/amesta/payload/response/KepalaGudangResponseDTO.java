package propensi.amesta.payload.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KepalaGudangResponseDTO {
    private UUID id;
    private String name;
    private String username;
    private String email;
}