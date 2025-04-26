package propensi.amesta.service.Purchase;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import propensi.amesta.payload.request.Purchase.PurchaseOrderRequestDTO;
import propensi.amesta.payload.response.Purchase.PurchaseOrderResponseDTO;

public interface PurchaseOrderService {
    PurchaseOrderResponseDTO addPurchaseOrder(PurchaseOrderRequestDTO request);
    List<PurchaseOrderResponseDTO> getAllPurchaseOrders(LocalDate startDate, LocalDate endDate, String status, UUID supplierId);
    PurchaseOrderResponseDTO getPurchaseOrderDetail(String purchaseOrderId);
}