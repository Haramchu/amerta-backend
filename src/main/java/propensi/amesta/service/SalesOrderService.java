package propensi.amesta.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import propensi.amesta.payload.request.SalesOrderRequestDTO;
import propensi.amesta.payload.response.SalesOrderDetailDTO;
import propensi.amesta.payload.response.SalesOrderListDTO;
import propensi.amesta.payload.response.SalesOrderResponseDTO;

public interface SalesOrderService {
    SalesOrderResponseDTO addSalesOrder(SalesOrderRequestDTO request);
    
    // Methods for viewing sales
    List<SalesOrderListDTO> getAllSalesOrders();
    List<SalesOrderListDTO> getSalesOrdersByDateRange(LocalDate startDate, LocalDate endDate);
    List<SalesOrderListDTO> getSalesOrdersByStatus(String status);
    List<SalesOrderListDTO> getSalesOrdersByCustomer(UUID customerId);
    List<SalesOrderListDTO> getSalesOrdersWithFilters(
        LocalDate startDate, LocalDate endDate, String status, UUID customerId);
    SalesOrderDetailDTO getSalesOrderDetail(String salesOrderId);
}