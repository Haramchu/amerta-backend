package propensi.amesta.payload.request.Sales;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalesOrderRequestDTO {

    @NotNull(message = "Customer ID tidak boleh kosong")
    private UUID customerId;

    @NotNull(message = "List barang tidak boleh kosong")
    private List<SalesOrderItemRequestDTO> items;

    @NotNull(message = "Tanggal order harus diisi")
    private LocalDate orderDate;
}
