package propensi.amesta.payload.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TambahKaryawanRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 100)
    private String password;

    @NotNull
    private boolean gender;

    @Size(max = 20)
    private String phone;

    @Size(max = 20)
    private String homePhone;

    @Size(max = 20)
    private String businessPhone;

    @Size(max = 20)
    private String whatsappNumber;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    private Date entryDate;

    @NotBlank
    @Size(max = 50)
    private String ktpNumber;

    @Size(max = 500)
    private String notes;

    @NotBlank
    @Size(max = 50)
    private String role;
}
