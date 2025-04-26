package propensi.amesta.service.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import propensi.amesta.model.Customer;
import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.Aset.Gudang;
import propensi.amesta.model.Purchase.Delivery;
import propensi.amesta.model.Purchase.PurchaseInvoice;
import propensi.amesta.model.Purchase.PurchaseOrder;
import propensi.amesta.model.Purchase.PurchaseOrderItem;
import propensi.amesta.model.Purchase.PurchasePayment;
import propensi.amesta.payload.request.Purchase.PurchaseOrderItemRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchaseOrderRequestDTO;
import propensi.amesta.payload.response.Purchase.DeliveryResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchaseInvoiceResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchaseOrderResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchaseOrderItemResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchasePaymentResponseDTO;
import propensi.amesta.repository.CustomerDb;
import propensi.amesta.repository.Aset.BarangDb;
import propensi.amesta.repository.Aset.GudangDb;
import propensi.amesta.repository.Purchase.PurchaseOrderDb;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private CustomerDb customerDb;

    @Autowired
    private BarangDb barangDb;

    @Autowired
    private PurchaseOrderDb purchaseOrderDb;

    @Autowired
    private GudangDb gudangDb;

    @Override
    public PurchaseOrderResponseDTO addPurchaseOrder(PurchaseOrderRequestDTO request) {
        // Validasi customer ada atau tidak, jika ada cek apakah vendor atau tidak
         Customer customer = customerDb.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer tidak ditemukan"));

        if (!customer.getRole().equalsIgnoreCase("VENDOR")){
            throw new IllegalArgumentException("Customer harus merupakan vendor");
        }
        
        // Validasi untuk tanggal pembelian tidak boleh di masa lalu
        LocalDate purchaseDate = request.getPurchaseDate();
        if (purchaseDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal pembelian tidak boleh di masa lalu");
        }

        // Validasi untuk tanggal invoice tidak boleh sebelum tanggal pembelian atau tidak boleh di masa lalu
        LocalDate invoiceDate = request.getInvoiceDate();
        if (invoiceDate.isBefore(purchaseDate) || invoiceDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal invoice tidak boleh sebelum tanggal pembelian");
        }

        // Validasi untuk tanggal pengiriman tidak boleh sebelum tanggal pembelian atau tidak boleh di masa lalu
        LocalDate deliveryDate = request.getDelivery().getDeliveryDate();
        if (deliveryDate.isBefore(purchaseDate) || deliveryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal pengiriman tidak boleh sebelum tanggal pembelian");
        }

        // Validasi untuk tanggal pembayaran tidak boleh sebelum tanggal pembelian atau tidak boleh di masa lalu
        LocalDate paymentDate = request.getPayment().getPaymentDate();
        if (paymentDate.isBefore(purchaseDate) || paymentDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal pembayaran tidak boleh sebelum tanggal pembelian");
        }

        // Validasi untuk barang harus ada dan kuantitas tidak boleh negatif
        for (PurchaseOrderItemRequestDTO item : request.getItems()) {
                if (!barangDb.existsById(item.getBarangId())) {
                        throw new IllegalArgumentException("Barang dengan ID " + item.getBarangId() + " tidak ditemukan");
                }

                if (item.getQuantity() <= 0) {
                        throw new IllegalArgumentException("Kuantitas barang tidak boleh negatif atau nol");
                }
        }

        // Validasi multiple entry, jika barang dan gudang tujuan sama di PurchaseOrderItem, kuantitas dijumlahkan
        Map<String, PurchaseOrderItemRequestDTO> itemMap = new HashMap<>();

        for (PurchaseOrderItemRequestDTO item : request.getItems()) {
            String key = item.getBarangId() + "|" + item.getGudangTujuan();

            if (itemMap.containsKey(key)) {
                PurchaseOrderItemRequestDTO existing = itemMap.get(key);
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
            } else {
                itemMap.put(key, new PurchaseOrderItemRequestDTO(
                    item.getBarangId(),
                    item.getQuantity(),
                    item.getGudangTujuan()
                ));
            }
        }

        List<PurchaseOrderItemRequestDTO> consolidatedItems = new ArrayList<>(itemMap.values());
        request.setItems(consolidatedItems);


        // Validasi gudang tujuan harus ada
        for (PurchaseOrderItemRequestDTO item : request.getItems()) {
                Gudang gudang = gudangDb.findById(item.getGudangTujuan())
                        .orElseThrow(() -> new IllegalArgumentException("Gudang tujuan dengan ID " + item.getGudangTujuan() + " tidak ditemukan"));
        }

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(generatePoId());
        purchaseOrder.setCustomer(customer);
        purchaseOrder.setStatus("CREATED");
        purchaseOrder.setPurchaseDate(request.getPurchaseDate());

        BigDecimal total = BigDecimal.ZERO;
        List<PurchaseOrderItem> items = new ArrayList<>();
        for (PurchaseOrderItemRequestDTO itemDTO : request.getItems()) {
                Barang barang = barangDb.findById(itemDTO.getBarangId())
                        .orElseThrow(() -> new IllegalArgumentException("Barang dengan ID " + itemDTO.getBarangId() + " tidak ditemukan"));

                BigDecimal unitPrice = barang.getHarga();
                BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
                total = total.add(itemTotal);

                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setId(UUID.randomUUID()); // ID = UUID
                item.setPurchaseOrder(purchaseOrder);
                item.setBarang(barang);
                item.setQuantity(itemDTO.getQuantity());
                item.setUnitPrice(unitPrice);
                item.setSubtotal(itemTotal);
                item.setGudangTujuan(gudangDb.findById(itemDTO.getGudangTujuan())
                        .orElseThrow(() -> new IllegalArgumentException("Gudang tujuan dengan ID " + itemDTO.getGudangTujuan() + " tidak ditemukan")));

                items.add(item);
        }

        purchaseOrder.setItems(items);

        PurchasePayment payment = new PurchasePayment();
        payment.setId(generatePaymentId(customer.getName()));
        payment.setPaymentDate(paymentDate);
        payment.setTotalAmountPayed(BigDecimal.valueOf(0)); // Belum ada total amount payed di tahap ini, karena belum ada pembayaran
        payment.setPaymentMethod(request.getPayment().getPaymentMethod());
        payment.setPaymentStatus("UNPAID"); // UNPAID, PAID, REFUNDED,
        payment.setPurchaseOrder(purchaseOrder);
        purchaseOrder.setPayment(payment);

        Delivery delivery = new Delivery();
        delivery.setId(generateDeliveryId(items)); // ID = UUID
        delivery.setDeliveryDate(deliveryDate);
        delivery.setDeliveryStatus("PENDING"); // PENDING, IN_PROGRESS, DELIVERED, CANCELLED
        delivery.setTrackingNumber(generateTrackingNumber(items));
        delivery.setDeliveryFee(request.getDelivery().getDeliveryFee());
        delivery.setPurchaseOrder(purchaseOrder);
        purchaseOrder.setDelivery(delivery);

        PurchaseInvoice invoice = new PurchaseInvoice();
        invoice.setId(generateInvoiceId());
        invoice.setInvoiceDate(invoiceDate);
        invoice.setInvoiceStatus("DRAFT"); // DRAFT,ISSUED, PAID
        invoice.setTotalAmount(total);
        invoice.setPurchaseOrder(purchaseOrder);
        purchaseOrder.setInvoice(invoice);

        return purchaseOrderToPurchaseOrderResponseDTO(purchaseOrderDb.save(purchaseOrder));
    }

    @Override
    public List<PurchaseOrderResponseDTO> getAllPurchaseOrders(
            LocalDate startDate, LocalDate endDate, String status, UUID supplierId) {

        // If no filters are applied, return all purchase orders
        if (startDate == null && endDate == null && status == null && supplierId == null) {
            List<PurchaseOrder> purchaseOrders = purchaseOrderDb.findAll();
            return purchaseOrders.stream()
                .map(this::purchaseOrderToPurchaseOrderResponseDTO)
                .collect(Collectors.toList());
        }

        // Set default date range if not provided
        if (startDate == null) {
            startDate = LocalDate.of(2000, 1, 1);
        }
        if (endDate == null) {
            endDate = LocalDate.now().plusYears(10);
        }

        List<PurchaseOrder> purchaseOrders;
        if (status != null && supplierId != null) {
            // Validate supplier
            Customer supplier = customerDb.findById(supplierId)
                    .orElseThrow(() -> new IllegalArgumentException("Supplier tidak ditemukan"));
            
            if (!supplier.getRole().equalsIgnoreCase("VENDOR")) {
                throw new IllegalArgumentException("Customer dengan ID tersebut bukan supplier/vendor");
            }
            
            purchaseOrders = purchaseOrderDb.findByPurchaseDateBetweenAndStatusAndCustomerId(startDate, endDate, status, supplierId);
        } else if (status != null) {
            purchaseOrders = purchaseOrderDb.findByPurchaseDateBetweenAndStatus(startDate, endDate, status);
        } else if (supplierId != null) {
            // Validate supplier
            Customer supplier = customerDb.findById(supplierId)
                    .orElseThrow(() -> new IllegalArgumentException("Supplier tidak ditemukan"));
            
            if (!supplier.getRole().equalsIgnoreCase("VENDOR")) {
                throw new IllegalArgumentException("Customer dengan ID tersebut bukan supplier/vendor");
            }
            
            purchaseOrders = purchaseOrderDb.findByPurchaseDateBetweenAndCustomerId(startDate, endDate, supplierId);
        } else {
            purchaseOrders = purchaseOrderDb.findByPurchaseDateBetween(startDate, endDate);
        }
        
        return purchaseOrders.stream()
            .map(this::purchaseOrderToPurchaseOrderResponseDTO)
            .collect(Collectors.toList());
    }

    @Override
    public PurchaseOrderResponseDTO getPurchaseOrderDetail(String purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderDb.findById(purchaseOrderId)
        .orElseThrow(() -> new IllegalArgumentException("Purchase order tidak ditemukan"));

        Customer supplier = purchaseOrder.getCustomer();
        if (supplier == null) {
            throw new IllegalArgumentException("Data supplier tidak ditemukan");
        }
        
        // Create main response DTO
        PurchaseOrderResponseDTO responseDTO = new PurchaseOrderResponseDTO();
        responseDTO.setId(purchaseOrder.getId());
        responseDTO.setCustomerId(supplier.getId());
        responseDTO.setPurchaseDate(purchaseOrder.getPurchaseDate());
        responseDTO.setStatus(purchaseOrder.getStatus());
        
        // Create invoice DTO
        if (purchaseOrder.getInvoice() != null) {
            PurchaseInvoiceResponseDTO invoiceDTO = new PurchaseInvoiceResponseDTO(
                purchaseOrder.getInvoice().getId(),
                purchaseOrder.getId(),
                purchaseOrder.getInvoice().getInvoiceDate(),
                purchaseOrder.getInvoice().getInvoiceStatus(),
                purchaseOrder.getInvoice().getTotalAmount()
            );
            responseDTO.setInvoice(invoiceDTO);
        }
        
        // Create delivery DTO
        if (purchaseOrder.getDelivery() != null) {
            DeliveryResponseDTO deliveryDTO = new DeliveryResponseDTO(
                purchaseOrder.getDelivery().getId(),
                purchaseOrder.getId(),
                purchaseOrder.getDelivery().getDeliveryDate(),
                purchaseOrder.getDelivery().getDeliveryStatus(),
                purchaseOrder.getDelivery().getTrackingNumber(),
                purchaseOrder.getDelivery().getDeliveryFee()
            );
            responseDTO.setDelivery(deliveryDTO);
        }
        
        // Create payment DTO
        if (purchaseOrder.getPayment() != null) {
            PurchasePaymentResponseDTO paymentDTO = new PurchasePaymentResponseDTO(
                purchaseOrder.getPayment().getId(),
                purchaseOrder.getId(),
                purchaseOrder.getPayment().getPaymentDate(),
                purchaseOrder.getPayment().getPaymentMethod(),
                purchaseOrder.getPayment().getPaymentStatus(),
                purchaseOrder.getPayment().getTotalAmountPayed()
            );
            responseDTO.setPayment(paymentDTO);
        }
        
        // Create items DTO list
        List<PurchaseOrderItemResponseDTO> itemDTOs = new ArrayList<>();
        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            PurchaseOrderItemResponseDTO itemDTO = new PurchaseOrderItemResponseDTO(
                item.getId(),
                purchaseOrder.getId(),
                item.getBarang().getId(),
                item.getQuantity(),
                item.getGudangTujuan().getNama()
            );
            itemDTOs.add(itemDTO);
        }
        responseDTO.setItems(itemDTOs);
        
        return responseDTO;
    }

    private PurchaseOrderResponseDTO purchaseOrderToPurchaseOrderResponseDTO(PurchaseOrder purchaseOrder) {
        PurchasePaymentResponseDTO payment = purchasePaymentToPurchasePaymentResponseDTO(purchaseOrder.getPayment());

        List<PurchaseOrderItemResponseDTO> items = new ArrayList<>();
        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            items.add(purchaseOrderItemToPurchaseOrderItemResponseDTO(item));
        }

        PurchaseInvoiceResponseDTO invoice = purchaseInvoiceToPurchaseInvoiceResponseDTO(purchaseOrder.getInvoice());

        DeliveryResponseDTO delivery = deliveryToDeliveryResponseDTO(purchaseOrder.getDelivery());

        return new PurchaseOrderResponseDTO(
                purchaseOrder.getId(),
                purchaseOrder.getCustomer().getId(),
                purchaseOrder.getPurchaseDate(),
                invoice,
                delivery,
                payment,
                purchaseOrder.getStatus(),
                items
        );
    }

    private PurchasePaymentResponseDTO purchasePaymentToPurchasePaymentResponseDTO(PurchasePayment purchasePayment) {
        return new PurchasePaymentResponseDTO(
                purchasePayment.getId(),
                purchasePayment.getPurchaseOrder().getId(),
                purchasePayment.getPaymentDate(),
                purchasePayment.getPaymentMethod(),
                purchasePayment.getPaymentStatus(),
                purchasePayment.getTotalAmountPayed()
        );
    }

    private PurchaseOrderItemResponseDTO purchaseOrderItemToPurchaseOrderItemResponseDTO(PurchaseOrderItem purchaseOrderItem) {
        return new PurchaseOrderItemResponseDTO(
                purchaseOrderItem.getId(),
                purchaseOrderItem.getPurchaseOrder().getId(),
                purchaseOrderItem.getBarang().getId(),
                purchaseOrderItem.getQuantity(),
                purchaseOrderItem.getGudangTujuan().getNama()
        );
    }

    private DeliveryResponseDTO deliveryToDeliveryResponseDTO(Delivery delivery) {
        return new DeliveryResponseDTO(
                delivery.getId(),
                delivery.getPurchaseOrder().getId(),
                delivery.getDeliveryDate(),
                delivery.getDeliveryStatus(),
                delivery.getTrackingNumber(),
                delivery.getDeliveryFee()
        );
    }

    private PurchaseInvoiceResponseDTO purchaseInvoiceToPurchaseInvoiceResponseDTO(PurchaseInvoice purchaseInvoice) {
        return new PurchaseInvoiceResponseDTO(
                purchaseInvoice.getId(),
                purchaseInvoice.getPurchaseOrder().getId(),
                purchaseInvoice.getInvoiceDate(),
                purchaseInvoice.getInvoiceStatus(),
                purchaseInvoice.getTotalAmount()
        );
    }
    
    private String generateTrackingNumber(List<PurchaseOrderItem> items) {
        // Format: UUID-XXX-Y, di mana UUID adalah 6 karakter acak, XXX adalah 3 huruf pertama dari nama barang, dan Y adalah 1 digit angka 
        String uuidPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        StringBuilder trackingNumber = new StringBuilder(uuidPart);

        String y = String.valueOf(items.size());

        trackingNumber.append("-").append(items.get(0).getBarang().getNama().substring(0, 3).toUpperCase()).append(y);

        return trackingNumber.toString();
    }

    private String generatePoId(){
        String id = "PO-";
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

        String uuidPart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        String poId = id + datePart + "-" + uuidPart;

        return poId;
    }

    private String generateInvoiceId(){
        String id = "INV-";
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

        String uuidPart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        String invoiceId = id + datePart + "-" + uuidPart;

        return invoiceId;
    }

    private String generateDeliveryId(List<PurchaseOrderItem> items) {
        String prefix = "DEL-";
    
        String namaBarang = items.get(0).getBarang().getNama();
        String kodeBarang = namaBarang.length() >= 3 ? namaBarang.substring(0, 3).toUpperCase() : namaBarang.toUpperCase();
    
        String tanggal = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM"))
            .replace("-01", "I")
            .replace("-02", "II")
            .replace("-03", "III")
            .replace("-04", "IV")
            .replace("-05", "V")
            .replace("-06", "VI")
            .replace("-07", "VII")
            .replace("-08", "VIII")
            .replace("-09", "IX")
            .replace("-10", "X")
            .replace("-11", "XI")
            .replace("-12", "XII");
    
        String randomPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5).toUpperCase();
    
        return prefix + kodeBarang + "-" + tanggal + "-" + randomPart;
    }

    private String generatePaymentId(String customerName) {
        String prefix = "PAY-";
    
        String kodeCustomer = customerName.length() >= 3
            ? customerName.substring(0, 3).toUpperCase().replace(" ", "")
            : customerName.toUpperCase().replace(" ", "");
    
        String monthPart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MM"));
        String romanMonth = switch (monthPart) {
            case "01" -> "I";
            case "02" -> "II";
            case "03" -> "III";
            case "04" -> "IV";
            case "05" -> "V";
            case "06" -> "VI";
            case "07" -> "VII";
            case "08" -> "VIII";
            case "09" -> "IX";
            case "10" -> "X";
            case "11" -> "XI";
            case "12" -> "XII";
            default -> "";
        };
        String year = String.valueOf(java.time.LocalDate.now().getYear()).substring(2); // ambil 2 digit terakhir
    
        String randomPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5).toUpperCase();
    
        return prefix + kodeCustomer + "-" + romanMonth + year + "-" + randomPart;
    }
}