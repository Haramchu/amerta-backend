package propensi.amesta.service.Sales;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import propensi.amesta.payload.request.Sales.SalesOrderRequestDTO;
import propensi.amesta.payload.response.Sales.SalesOrderDetailDTO;
import propensi.amesta.payload.response.Sales.SalesOrderResponseDTO;

public interface SalesOrderService {
    SalesOrderResponseDTO addSalesOrder(SalesOrderRequestDTO request);
    List<SalesOrderResponseDTO> getAllSalesOrders();
    List<SalesOrderResponseDTO> getSalesOrdersByDateRange(LocalDate startDate, LocalDate endDate);
    List<SalesOrderResponseDTO> getSalesOrdersByStatus(String status);
    List<SalesOrderResponseDTO> getSalesOrdersByCustomer(UUID customerId);
    List<SalesOrderResponseDTO> getSalesOrdersWithFilters(LocalDate startDate, LocalDate endDate, String status, UUID customerId);
    SalesOrderDetailDTO getSalesOrderDetail(String salesOrderId);
}