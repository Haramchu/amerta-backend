package propensi.amesta.service.Shipping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propensi.amesta.model.Customer;
import propensi.amesta.model.Shipping.ShippingDocument;
import propensi.amesta.model.Shipping.ShippingDocumentItem;
import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.Aset.Gudang;
import propensi.amesta.model.Purchase.PurchaseOrder;
import propensi.amesta.model.Purchase.PurchaseOrderItem;
import propensi.amesta.model.Sales.SalesOrder;
import propensi.amesta.model.Sales.SalesOrderItem;
import propensi.amesta.payload.request.Shipping.ShippingDocumentItemRequestDTO;
import propensi.amesta.payload.request.Shipping.ShippingDocumentRequestDTO;
import propensi.amesta.payload.response.Shipping.ShippingDocumentItemResponseDTO;
import propensi.amesta.payload.response.Shipping.ShippingDocumentResponseDTO;
import propensi.amesta.repository.CustomerDb;
import propensi.amesta.repository.Shipping.ShippingDocumentDb;
import propensi.amesta.repository.Shipping.ShippingDocumentItemDb;
import propensi.amesta.repository.Aset.BarangDb;
import propensi.amesta.repository.Aset.GudangDb;
import propensi.amesta.repository.Purchase.PurchaseOrderDb;
import propensi.amesta.repository.Sales.SalesOrderDb;

@Service
@Transactional
public class ShippingDocumentServiceImpl implements ShippingDocumentService {

    @Autowired
    private ShippingDocumentDb shippingDocumentDb;
    
    @Autowired
    private ShippingDocumentItemDb shippingDocumentItemDb;
    
    @Autowired
    private CustomerDb customerDb;
    
    @Autowired
    private BarangDb barangDb;
    
    @Autowired
    private GudangDb gudangDb;
    
    @Autowired
    private PurchaseOrderDb purchaseOrderDb;
    
    @Autowired
    private SalesOrderDb salesOrderDb;

    @Override
    public ShippingDocumentResponseDTO createShippingDocument(ShippingDocumentRequestDTO request) {
        Customer customer = customerDb.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer tidak ditemukan"));
        
        ShippingDocument shippingDocument = new ShippingDocument();
        shippingDocument.setId(generateShippingDocumentId());
        shippingDocument.setDocumentDate(request.getDocumentDate());
        shippingDocument.setPurchaseOrderId(request.getPurchaseOrderId());
        shippingDocument.setSalesOrderId(request.getSalesOrderId());
        shippingDocument.setCustomer(customer);
        shippingDocument.setShippingAddress(request.getShippingAddress());
        shippingDocument.setRecipientName(request.getRecipientName());
        shippingDocument.setStatus(request.getStatus());
        shippingDocument.setNotes(request.getNotes());
        
        ShippingDocument savedDocument = shippingDocumentDb.save(shippingDocument);
        
        List<ShippingDocumentItem> items = new ArrayList<>();
        for (ShippingDocumentItemRequestDTO itemDTO : request.getItems()) {
            Barang barang = barangDb.findById(itemDTO.getBarangId())
                    .orElseThrow(() -> new IllegalArgumentException("Barang tidak ditemukan"));
            
            Gudang gudang = gudangDb.findById(itemDTO.getGudangNama())
                    .orElseThrow(() -> new IllegalArgumentException("Gudang tidak ditemukan"));
            
            ShippingDocumentItem item = new ShippingDocumentItem();
            item.setShippingDocument(savedDocument);
            item.setBarang(barang);
            item.setQuantity(itemDTO.getQuantity());
            item.setGudang(gudang);
            
            items.add(item);
        }
        
        shippingDocumentItemDb.saveAll(items);
        
        return mapToShippingDocumentResponseDTO(savedDocument, items);
    }

    @Override
    public ShippingDocumentResponseDTO generateFromPurchaseOrder(String purchaseOrderId) {
        // Check if shipping document already exists for this purchase order
        if (shippingDocumentDb.findByPurchaseOrderId(purchaseOrderId).isPresent()) {
            throw new IllegalArgumentException("Surat jalan untuk Purchase Order ini sudah ada");
        }
        
        PurchaseOrder purchaseOrder = purchaseOrderDb.findById(purchaseOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order tidak ditemukan"));
        
        // Validate purchase order status
        if (!purchaseOrder.getStatus().equals("PAID") && !purchaseOrder.getStatus().equals("COMPLETED")) {
            throw new IllegalArgumentException("Purchase Order harus berstatus PAID atau COMPLETED");
        }
        
        ShippingDocument shippingDocument = new ShippingDocument();
        shippingDocument.setId(generateShippingDocumentId());
        shippingDocument.setDocumentDate(LocalDate.now());
        shippingDocument.setPurchaseOrderId(purchaseOrderId);
        shippingDocument.setCustomer(purchaseOrder.getCustomer());
        
        // Set shipping address and recipient from customer
        shippingDocument.setShippingAddress(purchaseOrder.getCustomer().getAddress());
        shippingDocument.setRecipientName(purchaseOrder.getCustomer().getName());
        shippingDocument.setStatus("DRAFT");
        shippingDocument.setNotes("Generated automatically from Purchase Order: " + purchaseOrderId);
        
        ShippingDocument savedDocument = shippingDocumentDb.save(shippingDocument);
        
        List<ShippingDocumentItem> items = new ArrayList<>();
        for (PurchaseOrderItem poItem : purchaseOrder.getItems()) {
            ShippingDocumentItem item = new ShippingDocumentItem();
            item.setShippingDocument(savedDocument);
            item.setBarang(poItem.getBarang());
            item.setQuantity(poItem.getQuantity());
            item.setGudang(poItem.getGudangTujuan());
            
            items.add(item);
        }
        
        shippingDocumentItemDb.saveAll(items);
        
        return mapToShippingDocumentResponseDTO(savedDocument, items);
    }

    @Override
    public ShippingDocumentResponseDTO generateFromSalesOrder(String salesOrderId) {
        // Check if shipping document already exists for this sales order
        if (shippingDocumentDb.findBySalesOrderId(salesOrderId).isPresent()) {
            throw new IllegalArgumentException("Surat jalan untuk Sales Order ini sudah ada");
        }
        
        SalesOrder salesOrder = salesOrderDb.findById(salesOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Sales Order tidak ditemukan"));
        
        // Validate sales order status
        if (!salesOrder.getStatus().equals("IN SHIPPING") && !salesOrder.getStatus().equals("SHIPPED") && 
            !salesOrder.getStatus().equals("COMPLETED")) {
            throw new IllegalArgumentException("Sales Order harus berstatus IN SHIPPING, SHIPPED, atau COMPLETED");
        }
        
        ShippingDocument shippingDocument = new ShippingDocument();
        shippingDocument.setId(generateShippingDocumentId());
        shippingDocument.setDocumentDate(LocalDate.now());
        shippingDocument.setSalesOrderId(salesOrderId);
        shippingDocument.setCustomer(salesOrder.getCustomer());
        
        // Set shipping address and recipient from customer
        shippingDocument.setShippingAddress(salesOrder.getCustomer().getAddress());
        shippingDocument.setRecipientName(salesOrder.getCustomer().getName());
        shippingDocument.setStatus("DRAFT");
        shippingDocument.setNotes("Generated automatically from Sales Order: " + salesOrderId);
        
        ShippingDocument savedDocument = shippingDocumentDb.save(shippingDocument);
        
        List<ShippingDocumentItem> items = new ArrayList<>();
        for (SalesOrderItem soItem : salesOrder.getItems()) {
            ShippingDocumentItem item = new ShippingDocumentItem();
            item.setShippingDocument(savedDocument);
            item.setBarang(soItem.getBarang());
            item.setQuantity(soItem.getQuantity());
            item.setGudang(soItem.getGudangTujuan());
            
            items.add(item);
        }
        
        shippingDocumentItemDb.saveAll(items);
        
        return mapToShippingDocumentResponseDTO(savedDocument, items);
    }

    @Override
    public List<ShippingDocumentResponseDTO> getAllShippingDocuments() {
        List<ShippingDocument> documents = shippingDocumentDb.findAll();
        return documents.stream()
                .map(document -> {
                    List<ShippingDocumentItem> items = shippingDocumentItemDb.findByShippingDocument(document);
                    return mapToShippingDocumentResponseDTO(document, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ShippingDocumentResponseDTO getShippingDocumentById(String id) {
        ShippingDocument document = shippingDocumentDb.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Surat jalan tidak ditemukan"));
        
        List<ShippingDocumentItem> items = shippingDocumentItemDb.findByShippingDocument(document);
        
        return mapToShippingDocumentResponseDTO(document, items);
    }

    @Override
    public ShippingDocumentResponseDTO updateStatus(String id, String status) {
        ShippingDocument document = shippingDocumentDb.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Surat jalan tidak ditemukan"));
        
        // Validate status
        if (!status.equals("DRAFT") && !status.equals("SUBMITTED") && !status.equals("COMPLETED")) {
            throw new IllegalArgumentException("Status tidak valid");
        }
        
        document.setStatus(status);
        ShippingDocument updatedDocument = shippingDocumentDb.save(document);
        
        List<ShippingDocumentItem> items = shippingDocumentItemDb.findByShippingDocument(updatedDocument);
        
        return mapToShippingDocumentResponseDTO(updatedDocument, items);
    }

    @Override
    public ShippingDocumentResponseDTO updateShippingDocument(String id, ShippingDocumentRequestDTO request) {
        ShippingDocument document = shippingDocumentDb.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Surat jalan tidak ditemukan"));
        
        // Only allow updates if status is DRAFT
        if (!document.getStatus().equals("DRAFT")) {
            throw new IllegalArgumentException("Surat jalan hanya dapat diupdate jika berstatus DRAFT");
        }
        
        Customer customer = customerDb.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer tidak ditemukan"));
        
        document.setDocumentDate(request.getDocumentDate());
        document.setCustomer(customer);
        document.setShippingAddress(request.getShippingAddress());
        document.setRecipientName(request.getRecipientName());
        document.setNotes(request.getNotes());
        
        ShippingDocument updatedDocument = shippingDocumentDb.save(document);
        
        // Delete existing items and create new ones
        shippingDocumentItemDb.deleteByShippingDocumentId(id);
        
        List<ShippingDocumentItem> items = new ArrayList<>();
        for (ShippingDocumentItemRequestDTO itemDTO : request.getItems()) {
            Barang barang = barangDb.findById(itemDTO.getBarangId())
                    .orElseThrow(() -> new IllegalArgumentException("Barang tidak ditemukan"));
            
            Gudang gudang = gudangDb.findById(itemDTO.getGudangNama())
                    .orElseThrow(() -> new IllegalArgumentException("Gudang tidak ditemukan"));
            
            ShippingDocumentItem item = new ShippingDocumentItem();
            item.setShippingDocument(updatedDocument);
            item.setBarang(barang);
            item.setQuantity(itemDTO.getQuantity());
            item.setGudang(gudang);
            
            items.add(item);
        }
        
        shippingDocumentItemDb.saveAll(items);
        
        return mapToShippingDocumentResponseDTO(updatedDocument, items);
    }

    @Override
    public List<ShippingDocumentResponseDTO> getShippingDocumentsWithFilters(String status, LocalDate startDate, LocalDate endDate, UUID customerId) {
        List<ShippingDocument> documents = shippingDocumentDb.findByFilters(status, startDate, endDate, customerId);
        
        return documents.stream()
                .map(document -> {
                    List<ShippingDocumentItem> items = shippingDocumentItemDb.findByShippingDocument(document);
                    return mapToShippingDocumentResponseDTO(document, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteShippingDocument(String id) {
        ShippingDocument document = shippingDocumentDb.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Surat jalan tidak ditemukan"));
        
        // Only allow deletion if status is DRAFT
        if (!document.getStatus().equals("DRAFT")) {
            throw new IllegalArgumentException("Surat jalan hanya dapat dihapus jika berstatus DRAFT");
        }
        
        // Delete items first to avoid foreign key constraint violations
        shippingDocumentItemDb.deleteByShippingDocumentId(id);
        shippingDocumentDb.delete(document);
    }
    
    private String generateShippingDocumentId() {
        // Format: SJ-YYYY-MM-DD-XXXXX
        String prefix = "SJ-";
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .replace("-01-", "-I-")
                .replace("-02-", "-II-")
                .replace("-03-", "-III-")
                .replace("-04-", "-IV-")
                .replace("-05-", "-V-")
                .replace("-06-", "-VI-")
                .replace("-07-", "-VII-")
                .replace("-08-", "-VIII-")
                .replace("-09-", "-IX-")
                .replace("-10-", "-X-")
                .replace("-11-", "-XI-")
                .replace("-12-", "-XII-");
        
        String randomPart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        
        return prefix + datePart + "-" + randomPart;
    }
    
    private ShippingDocumentResponseDTO mapToShippingDocumentResponseDTO(ShippingDocument document, List<ShippingDocumentItem> items) {
        List<ShippingDocumentItemResponseDTO> itemResponseDTOs = items.stream()
                .map(this::mapToShippingDocumentItemResponseDTO)
                .collect(Collectors.toList());
        
        return new ShippingDocumentResponseDTO(
                document.getId(),
                document.getDocumentDate(),
                document.getPurchaseOrderId(),
                document.getSalesOrderId(),
                document.getCustomer().getId(),
                document.getCustomer().getName(),
                document.getShippingAddress(),
                document.getRecipientName(),
                document.getStatus(),
                document.getNotes(),
                itemResponseDTOs,
                document.getCreatedDate(),
                document.getUpdatedDate()
        );
    }
    
    private ShippingDocumentItemResponseDTO mapToShippingDocumentItemResponseDTO(ShippingDocumentItem item) {
        return new ShippingDocumentItemResponseDTO(
                item.getId(),
                item.getShippingDocument().getId(),
                item.getBarang().getId(),
                item.getBarang().getNama(),
                item.getBarang().getMerk(),
                item.getQuantity(),
                item.getGudang().getNama()
        );
    }
}