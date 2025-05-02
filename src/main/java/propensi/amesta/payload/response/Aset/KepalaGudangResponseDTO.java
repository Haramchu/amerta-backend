package propensi.amesta.payload.response.Aset;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import propensi.amesta.payload.response.UserResponseDTO;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class KepalaGudangResponseDTO extends UserResponseDTO {}