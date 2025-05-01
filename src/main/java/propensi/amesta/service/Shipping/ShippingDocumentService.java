package propensi.amesta.service.Shipping;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import propensi.amesta.payload.request.Shipping.ShippingDocumentRequestDTO;
import propensi.amesta.payload.response.Shipping.ShippingDocumentResponseDTO;

public interface ShippingDocumentService {
    
    // Create shipping document
    ShippingDocumentResponseDTO createShippingDocument(ShippingDocumentRequestDTO request);
    
    // Generate shipping document from purchase order
    ShippingDocumentResponseDTO generateFromPurchaseOrder(String purchaseOrderId);
    
    // Generate shipping document from sales order
    ShippingDocumentResponseDTO generateFromSalesOrder(String salesOrderId);
    
    // Get all shipping documents
    List<ShippingDocumentResponseDTO> getAllShippingDocuments();
    
    // Get shipping document by ID
    ShippingDocumentResponseDTO getShippingDocumentById(String id);
    
    // Update shipping document status
    ShippingDocumentResponseDTO updateStatus(String id, String status);
    
    // Update shipping document
    ShippingDocumentResponseDTO updateShippingDocument(String id, ShippingDocumentRequestDTO request);
    
    // Get shipping documents with filters
    List<ShippingDocumentResponseDTO> getShippingDocumentsWithFilters(String status, LocalDate startDate, LocalDate endDate, UUID customerId);
    
    // Delete shipping document
    void deleteShippingDocument(String id);
}