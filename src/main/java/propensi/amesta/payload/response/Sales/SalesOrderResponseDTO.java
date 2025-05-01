package propensi.amesta.payload.response.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalesOrderResponseDTO {
    private String id;
    private UUID customerId;
    private LocalDate salesDate;
    private String status;
    private List<SalesOrderItemResponseDTO> items;
    private BigDecimal totalPrice;

    // Invoice (Faktur), untuk Ricky
    private SalesInvoiceResponseDTO invoice;

    // Shipping (Surat Jalan), untuk Jess
    private ShippingResponseDTO shipping;

    // Receipt (Nota), untuk Michael
    private SalesReceiptResponseDTO receipt;

    private SalesPaymentResponseDTO payment; 
}