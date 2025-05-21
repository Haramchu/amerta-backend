package propensi.amesta.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeRequestDTO {

    @NotEmpty(message = "Nama harus diisi")
    private String name;

    @NotEmpty(message = "Username harus diisi")
    private String username;

    @NotEmpty(message = "Email harus diisi")
    @Email(message = "Format email tidak valid")
    private String email;

    private boolean gender;

    @NotEmpty(message = "Nomor telepon harus diisi")
    private String phone;

    private String homePhone;
    private String businessPhone;
    private String whatsappNumber;

    private Date entryDate;

    @NotEmpty(message = "Nomor KTP harus diisi")
    private String ktpNumber;

    private String notes;
} 