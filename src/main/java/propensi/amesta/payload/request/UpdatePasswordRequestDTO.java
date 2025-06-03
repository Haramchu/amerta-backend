package propensi.amesta.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String password;
}
