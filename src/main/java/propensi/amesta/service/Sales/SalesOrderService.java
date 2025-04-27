package propensi.amesta.service.Sales;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import propensi.amesta.payload.request.Sales.SalesOrderRequestDTO;
import propensi.amesta.payload.response.Sales.SalesOrderDetailDTO;
import propensi.amesta.payload.response.Sales.SalesOrderResponseDTO;

public interface SalesOrderService {
    SalesOrderResponseDTO addSalesOrder(SalesOrderRequestDTO request);
    List<SalesOrderResponseDTO> getAllSalesOrders(LocalDate startDate, LocalDate endDate, String status, UUID customerId);
    SalesOrderDetailDTO getSalesOrderById(String salesOrderId);
}