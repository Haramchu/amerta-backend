package propensi.amesta.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequestDTO {

    @NotEmpty(message = "Nama harus diisi")
    private String name;

    @NotEmpty(message = "Nomor telepon harus diisi")
    private String phone;

    @NotEmpty(message = "Nomor handphone harus diisi")
    private String handphone;

    @NotEmpty(message = "Nomor WhatsApp harus diisi")
    private String whatsapp;

    @NotEmpty(message = "Email harus diisi")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotEmpty(message = "Alamat harus diisi")
    private String address;
} 