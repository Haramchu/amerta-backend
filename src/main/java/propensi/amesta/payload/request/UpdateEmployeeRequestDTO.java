package propensi.amesta.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeRequestDTO {

    @NotNull(message = "Nama harus diisi")
    @Size(max = 100, message = "Nama maksimal 100 karakter")
    private String name;

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

    @NotNull(message = "Nomor KTP harus diisi")
    @Size(max = 50, message = "Nomor KTP maksimal 50 karakter")
    private String ktpNumber;

    @Size(max = 500, message = "Catatan maksimal 500 karakter")
    private String notes;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    private Date birthDate;

    @NotNull(message = "Status karyawan harus diisi")
    private boolean employeeStatus;

    @NotBlank(message = "Role harus diisi")
    @Size(max = 50)
    private String role;
} 