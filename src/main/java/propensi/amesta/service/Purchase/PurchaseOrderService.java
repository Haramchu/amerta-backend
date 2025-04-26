package propensi.amesta.service.Purchase;

import java.util.List;

import propensi.amesta.payload.request.Purchase.DeliveryRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchaseOrderInvoiceRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchaseOrderRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchasePaymentRequestDTO;
import propensi.amesta.payload.response.Purchase.PurchaseOrderResponseDTO;

public interface PurchaseOrderService {
    PurchaseOrderResponseDTO addPurchaseOrder(PurchaseOrderRequestDTO request);
    List<PurchaseOrderResponseDTO> getAllPurchaseOrders();
    PurchaseOrderResponseDTO getPurchaseOrderById(String id);

    // UPDATE PURCHASE ORDER
    PurchaseOrderResponseDTO confirmPurchaseOrder(String id, PurchaseOrderInvoiceRequestDTO request);
    PurchaseOrderResponseDTO payPurchaseOrder(String id, PurchasePaymentRequestDTO request);
    PurchaseOrderResponseDTO deliverPurchaseOrder(String id, DeliveryRequestDTO request);
    PurchaseOrderResponseDTO completePurchaseOrder(String id);
}
