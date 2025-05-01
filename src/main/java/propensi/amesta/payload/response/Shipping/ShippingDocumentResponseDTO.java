package propensi.amesta.payload.response.Shipping;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDocumentResponseDTO {
    private String id;
    private LocalDate documentDate;
    private String purchaseOrderId;    
    private String salesOrderId;
    private UUID customerId;
    private String customerName;
    private String shippingAddress;
    private String recipientName;
    private String status;
    private String notes;
    private List<ShippingDocumentItemResponseDTO> items;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date createdDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date updatedDate;
}