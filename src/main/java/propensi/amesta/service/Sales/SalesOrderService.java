package propensi.amesta.service.Sales;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import propensi.amesta.payload.request.Sales.ShippingRequestDTO;
import propensi.amesta.payload.request.Sales.SalesOrderInvoiceRequestDTO;
import propensi.amesta.payload.request.Sales.SalesOrderRequestDTO;
import propensi.amesta.payload.request.Sales.SalesPaymentRequestDTO;
import propensi.amesta.payload.response.Sales.SalesOrderResponseDTO;

public interface SalesOrderService {
    SalesOrderResponseDTO addSalesOrder(SalesOrderRequestDTO request);

    // VIEW SALES ORDER
    List<SalesOrderResponseDTO> getAllSalesOrders(LocalDate startDate, LocalDate endDate, String status, UUID customerId);
    SalesOrderResponseDTO getSalesOrderById(String id);

    // UPDATE SALES ORDER
    SalesOrderResponseDTO confirmShipping(String id);
    SalesOrderResponseDTO confirmSalesOrder(String id, SalesOrderInvoiceRequestDTO request);
    SalesOrderResponseDTO paySalesOrder(String id, SalesPaymentRequestDTO request);
    SalesOrderResponseDTO shipSalesOrder(String id, ShippingRequestDTO request);
}