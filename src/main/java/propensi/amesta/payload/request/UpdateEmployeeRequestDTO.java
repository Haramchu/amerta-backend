package propensi.amesta.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeRequestDTO {

    @NotNull(message = "Nama harus diisi")
    @Size(max = 100, message = "Nama maksimal 100 karakter")
    private String name;

    @NotNull(message = "Username harus diisi")
    @Size(max = 100, message = "Username maksimal 100 karakter")
    private String username;

    @NotNull(message = "Email harus diisi")
    @Size(max = 50, message = "Email maksimal 50 karakter")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotNull(message = "Gender harus diisi")
    private boolean gender;

    @Size(max = 20, message = "Nomor telepon maksimal 20 karakter")
    private String phone;

    @Size(max = 20, message = "Nomor telepon rumah maksimal 20 karakter")
    private String homePhone;

    @Size(max = 20, message = "Nomor telepon bisnis maksimal 20 karakter")
    private String businessPhone;

    @Size(max = 20, message = "Nomor WhatsApp maksimal 20 karakter")
    private String whatsappNumber;

    @NotNull(message = "Tanggal masuk harus diisi")
    private Date entryDate;

    @NotNull(message = "Nomor KTP harus diisi")
    @Size(max = 50, message = "Nomor KTP maksimal 50 karakter")
    private String ktpNumber;

    @Size(max = 500, message = "Catatan maksimal 500 karakter")
    private String notes;

    private Date birthDate;

    @NotNull(message = "Status karyawan harus diisi")
    private boolean employeeStatus;
} 