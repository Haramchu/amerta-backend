package propensi.amesta.service;

import propensi.amesta.payload.request.SalesOrderRequestDTO;
import propensi.amesta.payload.response.SalesOrderResponseDTO;

public interface SalesOrderService {
    SalesOrderResponseDTO addSalesOrder(SalesOrderRequestDTO request);
}
