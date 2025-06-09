package propensi.amesta.payload.response.Auth;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtractJwtResponseDTO {

    private UUID id;

    private String username;

    private String email;

    private String role;

    private String token;

    private String nama;
}
