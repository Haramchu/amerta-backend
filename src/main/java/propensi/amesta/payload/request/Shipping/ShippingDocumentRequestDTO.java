package propensi.amesta.payload.request.Shipping;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDocumentRequestDTO {
    
    @NotNull(message = "Tanggal dokumen tidak boleh kosong")
    private LocalDate documentDate;
    
    private String purchaseOrderId;
    
    private String salesOrderId;
    
    @NotNull(message = "Customer ID tidak boleh kosong")
    private UUID customerId;
    
    @NotEmpty(message = "Alamat pengiriman tidak boleh kosong")
    private String shippingAddress;
    
    @NotEmpty(message = "Nama penerima tidak boleh kosong")
    private String recipientName;
    
    @NotEmpty(message = "Status dokumen tidak boleh kosong")
    private String status;
    
    private String notes;
    
    @Valid
    private List<ShippingDocumentItemRequestDTO> items;
}