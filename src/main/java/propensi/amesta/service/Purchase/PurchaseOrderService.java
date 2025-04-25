package propensi.amesta.service.Purchase;

import propensi.amesta.payload.request.Purchase.PurchaseOrderRequestDTO;
import propensi.amesta.payload.response.Purchase.PurchaseOrderResponseDTO;

public interface PurchaseOrderService {
    PurchaseOrderResponseDTO addPurchaseOrder(PurchaseOrderRequestDTO request);

}
