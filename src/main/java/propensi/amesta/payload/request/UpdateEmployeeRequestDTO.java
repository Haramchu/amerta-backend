package propensi.amesta.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeRequestDTO {

    @NotEmpty(message = "Email harus diisi")
    @Email(message = "Format email tidak valid")
    private String email;

    @Size(max = 20, message = "Nomor telepon maksimal 20 karakter")
    private String phone;

    @Size(max = 20, message = "Nomor telepon rumah maksimal 20 karakter")
    private String homePhone;

    @Size(max = 20, message = "Nomor telepon bisnis maksimal 20 karakter")
    private String businessPhone;

    @Size(max = 20, message = "Nomor WhatsApp maksimal 20 karakter")
    private String whatsappNumber;

    @Size(max = 500, message = "Catatan maksimal 500 karakter")
    private String notes;

    @NotNull(message = "Status karyawan harus diisi")
    private boolean employeeStatus;

    @NotBlank(message = "Role harus diisi")
    @Size(max = 50)
    private String role;
} 