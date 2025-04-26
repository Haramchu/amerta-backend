package propensi.amesta.service.Purchase;

import java.util.List;

import propensi.amesta.payload.request.Purchase.PurchaseOrderInvoiceRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchaseOrderRequestDTO;
import propensi.amesta.payload.response.Purchase.PurchaseOrderResponseDTO;

public interface PurchaseOrderService {
    PurchaseOrderResponseDTO addPurchaseOrder(PurchaseOrderRequestDTO request);
    List<PurchaseOrderResponseDTO> getAllPurchaseOrders();
    PurchaseOrderResponseDTO getPurchaseOrderById(String id);
    PurchaseOrderResponseDTO confirmPurchaseOrder(String id, PurchaseOrderInvoiceRequestDTO invoice);
}
