package propensi.amesta.service.Sales;

import java.util.List;

import propensi.amesta.payload.request.Sales.ShippingRequestDTO;
import propensi.amesta.payload.request.Sales.SalesOrderInvoiceRequestDTO;
import propensi.amesta.payload.request.Sales.SalesOrderRequestDTO;
import propensi.amesta.payload.request.Sales.SalesPaymentRequestDTO;
import propensi.amesta.payload.response.Sales.SalesOrderResponseDTO;

public interface SalesOrderService {
    SalesOrderResponseDTO addSalesOrder(SalesOrderRequestDTO request);
    List<SalesOrderResponseDTO> getAllSalesOrders();
    SalesOrderResponseDTO getSalesOrderById(String id);

    SalesOrderResponseDTO confirmShipping(String id);
    SalesOrderResponseDTO confirmSalesOrder(String id, SalesOrderInvoiceRequestDTO request);
    SalesOrderResponseDTO paySalesOrder(String id, SalesPaymentRequestDTO request);
    SalesOrderResponseDTO shipSalesOrder(String id, ShippingRequestDTO request);
}