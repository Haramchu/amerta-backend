package propensi.amesta.service;

import propensi.amesta.payload.request.Sales.SalesOrderRequestDTO;
import propensi.amesta.payload.response.SalesOrderResponseDTO;

public interface SalesOrderService {
    SalesOrderResponseDTO addSalesOrder(SalesOrderRequestDTO request);
}
