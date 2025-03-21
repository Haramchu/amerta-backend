package propensi.amesta.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginJwtResponseDTO {
    private String token;
    private String name;
}
