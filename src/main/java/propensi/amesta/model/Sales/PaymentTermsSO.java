package propensi.amesta.model.Sales;

import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "payment_terms_so")
public class PaymentTermsSO {
    @Id
    private UUID id;

    @NotNull(message = "Payment terms tidak boleh kosong")
    private Integer paymentTerms; // 30 days, 60 days, dll

    @NotNull(message = "Status payment terms tidak boleh kosong")
    private boolean status; // true = aktif, false = tidak aktif
    
    @NotNull(message = "Keterangan payment terms tidak boleh kosong")
    private String description;
}
