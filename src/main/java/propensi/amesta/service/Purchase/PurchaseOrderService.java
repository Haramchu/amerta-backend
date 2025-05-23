package propensi.amesta.service.Purchase;

import java.util.List;
import java.util.UUID;

import propensi.amesta.payload.request.Purchase.DeliveryRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchaseOrderInvoiceRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchaseOrderRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchasePaymentRequestDTO;
import propensi.amesta.payload.response.Purchase.PurchaseOrderResponseDTO;

public interface PurchaseOrderService {
    PurchaseOrderResponseDTO addPurchaseOrder(PurchaseOrderRequestDTO request);
    List<PurchaseOrderResponseDTO> getAllPurchaseOrders();
    List<PurchaseOrderResponseDTO> getPurchaseOrdersByStatus(String status);
    PurchaseOrderResponseDTO getPurchaseOrderById(String id);

    PurchaseOrderResponseDTO confirmPurchaseOrder(String id, PurchaseOrderInvoiceRequestDTO request);
    PurchaseOrderResponseDTO payPurchaseOrder(String id, PurchasePaymentRequestDTO request);
    PurchaseOrderResponseDTO deliverPurchaseOrder(String id, DeliveryRequestDTO request);
    PurchaseOrderResponseDTO completePurchaseOrder(String id);

    List<PurchaseOrderResponseDTO> getPurchaseOrdersbyVendor(UUID vendorId);
}