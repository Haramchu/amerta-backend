package propensi.amesta.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {
    private UUID id;
    private String name;
    private String phone;
    private String handphone;
    private String whatsapp;
    private String email;
    private String address;
    private String role;
}
