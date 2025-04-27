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

import propensi.amesta.enums.Purchase.DeliveryStatus;
import propensi.amesta.enums.Purchase.InvoiceStatus;
import propensi.amesta.enums.Purchase.PaymentStatus;
import propensi.amesta.enums.Purchase.PurchaseOrderStatus;
import propensi.amesta.model.Customer;
import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.Aset.StockBarangPerGudang;
import propensi.amesta.model.Purchase.Delivery;
import propensi.amesta.model.Purchase.PurchaseInvoice;
import propensi.amesta.model.Purchase.PurchaseOrder;
import propensi.amesta.model.Purchase.PurchaseOrderItem;
import propensi.amesta.model.Purchase.PurchasePayment;
import propensi.amesta.model.Purchase.PurchaseReceipt;
import propensi.amesta.payload.request.Purchase.DeliveryRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchaseOrderInvoiceRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchaseOrderItemRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchaseOrderRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchasePaymentRequestDTO;
import propensi.amesta.payload.response.Purchase.DeliveryResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchaseInvoiceResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchaseOrderResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchaseOrderItemResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchasePaymentResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchaseReceiptResponseDTO;
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


    // STAGE 1: CREATED
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

        // Validasi untuk barang harus ada dan kuantitas tidak boleh negatif
        for (PurchaseOrderItemRequestDTO item : request.getItems()) {
                if (!barangDb.existsById(item.getBarangId())) {
                        throw new IllegalArgumentException("Barang dengan ID " + item.getBarangId() + " tidak ditemukan");
                }

                if (item.getQuantity() <= 0) {
                        throw new IllegalArgumentException("Kuantitas barang tidak boleh negatif atau nol");
                }
        }

        // Validasi multiple entry, jika barang dan gudang tujuan sama di PurchaseOrderItem, kuantitas dijumlahkan, validasi pajaknya juga untuk barang yang dan gudang tujuan sama, pajak juga harus sama
        Map<String, PurchaseOrderItemRequestDTO> itemMap = new HashMap<>();

        for (PurchaseOrderItemRequestDTO item : request.getItems()) {
            String key = item.getBarangId() + "|" + item.getGudangTujuan();

            if (itemMap.containsKey(key)) {
                PurchaseOrderItemRequestDTO existing = itemMap.get(key);

                // Validasi pajak harus sama
                if (!existing.getPajak().equals(item.getPajak())) {
                    throw new IllegalArgumentException("Barang dengan ID " + item.getBarangId() + " dan gudang tujuan " + item.getGudangTujuan() + " memiliki pajak yang berbeda");
                }

                // Kalau pajak sama, jumlahkan quantity
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
            } else {
                itemMap.put(key, new PurchaseOrderItemRequestDTO(
                    item.getBarangId(),
                    item.getQuantity(),
                    item.getGudangTujuan(),
                    item.getPajak()
                ));
            }
        }

        List<PurchaseOrderItemRequestDTO> consolidatedItems = new ArrayList<>(itemMap.values());
        request.setItems(consolidatedItems);


        // Validasi gudang tujuan harus ada
        for (PurchaseOrderItemRequestDTO item : request.getItems()) {
            gudangDb.findById(item.getGudangTujuan())
                    .orElseThrow(() -> new IllegalArgumentException("Gudang tujuan dengan ID " + item.getGudangTujuan() + " tidak ditemukan"));
        }

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(generatePoId());
        purchaseOrder.setCustomer(customer);
        purchaseOrder.setStatus(PurchaseOrderStatus.CREATED);
        purchaseOrder.setPurchaseDate(request.getPurchaseDate());

        BigDecimal total = BigDecimal.ZERO;
        List<PurchaseOrderItem> items = new ArrayList<>();
        for (PurchaseOrderItemRequestDTO itemDTO : request.getItems()) {
                Barang barang = barangDb.findById(itemDTO.getBarangId())
                        .orElseThrow(() -> new IllegalArgumentException("Barang dengan ID " + itemDTO.getBarangId() + " tidak ditemukan"));

                BigDecimal unitPrice = barang.getHargaBeli(); // harga beli barang
                BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
                Integer pajak = itemDTO.getPajak(); // pajak dalam persen
                if (pajak == 0) {
                    total = total.add(itemTotal);
                }
                else{
                    BigDecimal pajakValue = itemTotal.multiply(BigDecimal.valueOf(pajak)).divide(BigDecimal.valueOf(100));
                    total = total.add(itemTotal.add(pajakValue));
                }

                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setId(UUID.randomUUID()); // ID = UUID
                item.setPurchaseOrder(purchaseOrder);
                item.setBarang(barang);
                item.setQuantity(itemDTO.getQuantity());
                item.setTax(pajak);
                item.setGudangTujuan(gudangDb.findById(itemDTO.getGudangTujuan()) // gaperlu dari dto, bisa juga dari barangnya langsung (asumsi gamau bisa ganti gudang tujuan di po)
                item.setUnitPrice(unitPrice);
                item.setSubtotal(itemTotal);
                item.setGudangTujuan(gudangDb.findById(itemDTO.getGudangTujuan())
                        .orElseThrow(() -> new IllegalArgumentException("Gudang tujuan dengan ID " + itemDTO.getGudangTujuan() + " tidak ditemukan")));

                items.add(item);
        }

        purchaseOrder.setItems(items);
        purchaseOrder.setTotalPrice(total);

        // TODO: implementasi security di websecurityconfig!!!!!!!!!!!

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
    public PurchaseOrderResponseDTO getPurchaseOrderById(String id) {
        PurchaseOrder purchaseOrder = purchaseOrderDb.findById(id)
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
        PurchasePaymentResponseDTO payment = null;
        PurchaseInvoiceResponseDTO invoice = null;
        DeliveryResponseDTO delivery = null;
        PurchaseReceiptResponseDTO receipt = null;

        if(purchaseOrder.getPayment() != null) {
            payment = purchasePaymentToPurchasePaymentResponseDTO(purchaseOrder.getPayment());
        }

        if(purchaseOrder.getInvoice() != null) {
            invoice = purchaseInvoiceToPurchaseInvoiceResponseDTO(purchaseOrder.getInvoice());
        }

        if(purchaseOrder.getDelivery() != null) {
            delivery = deliveryToDeliveryResponseDTO(purchaseOrder.getDelivery());
        }

        if(purchaseOrder.getReceipt() != null) {
            receipt = purchaseReceiptToPurchaseReceiptResponseDTO(purchaseOrder.getReceipt());
        }

        List<PurchaseOrderItemResponseDTO> items = new ArrayList<>();
        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            items.add(purchaseOrderItemToPurchaseOrderItemResponseDTO(item));
        }
        
        return new PurchaseOrderResponseDTO(
                purchaseOrder.getId(),
                purchaseOrder.getCustomer().getId(),
                purchaseOrder.getPurchaseDate(),
                purchaseOrder.getStatus(),
                items,
                purchaseOrder.getTotalPrice(),
                invoice,
                delivery,
                receipt,
                payment
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
        BigDecimal remainingAmount = purchaseInvoice.getTotalAmount();

        if (purchaseInvoice.getPurchaseOrder().getPayment() != null) {
            remainingAmount = purchaseInvoice.getTotalAmount().subtract(purchaseInvoice.getPurchaseOrder().getPayment().getTotalAmountPayed());
        } else {
            remainingAmount = purchaseInvoice.getTotalAmount();
        }

        return new PurchaseInvoiceResponseDTO(
                purchaseInvoice.getId(),
                purchaseInvoice.getPurchaseOrder().getId(),
                purchaseInvoice.getInvoiceDate(),
                purchaseInvoice.getInvoiceStatus(),
                purchaseInvoice.getTotalAmount(),
                purchaseInvoice.getPaymentTerms(),
                purchaseInvoice.getDueDate(),
                remainingAmount
        );
    }

    private PurchaseReceiptResponseDTO purchaseReceiptToPurchaseReceiptResponseDTO(PurchaseReceipt purchaseReceipt) {
        return new PurchaseReceiptResponseDTO(
                purchaseReceipt.getId(),
                purchaseReceipt.getPurchaseOrder().getId(),
                purchaseReceipt.getReceiptDate(),
                purchaseReceipt.getAmountPayed()
        );
    }
    
    private String generateTrackingNumber(List<PurchaseOrderItem> items) {
        // Format: UUID-XXX-Y, di mana UUID adalah 6 karakter acak, XXX adalah 3 huruf pertama dari nama barang, dan Y adalah 1 digit angka 
        String uuidPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        StringBuilder trackingNumber = new StringBuilder(uuidPart);

        String y = String.valueOf(items.size());

        trackingNumber.append("-").append(items.get(0).getBarang().getNama().replace(" ", "").substring(0, 3).toUpperCase()).append(y);

        return trackingNumber.toString();
    }

    public String generatePoId(){
        // Format: PO-YYYY-MM-DD-XXXXX, di mana YYYY adalah tahun, MM adalah bulan dalam angka romawi, DD adalah tanggal, dan XXXXX adalah 5 karakter acak
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
        // Format: INV-YYYY-MM-DD-XXXXX, di mana YYYY adalah tahun, MM adalah bulan dalam angka romawi, DD adalah tanggal, dan XXXXX adalah 5 karakter acak
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

    public String generateDeliveryId(List<PurchaseOrderItem> items) {
        // Format: DEL-XXX-DD-MM-XXXXX, di mana XXX adalah 3 huruf pertama dari nama barang, DD adalah tanggal dalam angka romawi, MM adalah bulan dalam angka romawi, dan XXXXX adalah 5 karakter acak
    private String generateDeliveryId(List<PurchaseOrderItem> items) {
        String prefix = "DEL-";
    
        String namaBarang = items.get(0).getBarang().getNama().replace(" ", "");
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

    public String generatePaymentId(String customerName) {
        // Format: PAY-XXX-MM-YY-XXXXX, di mana XXX adalah 3 huruf pertama dari nama customer, MM adalah bulan dalam angka romawi, YY adalah tahun dalam 2 digit, dan XXXXX adalah 5 karakter acak
    private String generatePaymentId(String customerName) {
        String prefix = "PAY-";
    
        String kodeCustomer = customerName.replace(" ", "").length() >= 3
            ? customerName.replace(" ", "").substring(0, 3).toUpperCase()
            : customerName.replace(" ", "").toUpperCase();
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
        String year = String.valueOf(java.time.LocalDate.now().getYear()).substring(2);
    
        String randomPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5).toUpperCase();
    
        return prefix + kodeCustomer + "-" + romanMonth + year + "-" + randomPart;
    }

    public String generateReceiptId(List<PurchaseOrderItem> items) {
        // Format: REC-XXX-DDMMYY-XXXXX
    
        String prefix = "REC-";
    
        String namaBarang = items.get(0).getBarang().getNama().replace(" ", "");
        String kodeBarang = namaBarang.length() >= 3
                ? namaBarang.substring(0, 3).toUpperCase()
                : namaBarang.toUpperCase();
    
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyy"));
    
        String randomPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5).toUpperCase();
    
        return prefix + kodeBarang + "-" + datePart + "-" + randomPart;
    }

    // Stages: 
    // 1. CREATED = PO baru saja dibuat, menunggu konfirmasi dari vendor
    // 2. CONFIRMED = PO sudah dikonfirmasi oleh vendor, menunggu pembayaran dari pembeli, muncul faktur
    // 3. PAID = Pembeli sudah melakukan pembayaran, menunggu pengiriman dari vendor, tahap masukin pembayaran
    // 4. IN DELIVERY = Barang sedang dikirim oleh pembeli, menunggu nota pembelian dari vendor, muncul surat jalan
    // 5. COMPLETED = Nota sudah diterima oleh pembeli, PO selesai
    
    // STAGE 2: CONFIRMED, FAKTUR (RICKY)
    @Override
    public PurchaseOrderResponseDTO confirmPurchaseOrder(String id, PurchaseOrderInvoiceRequestDTO request) {
        // Validasi untuk purchase order ada atau tidak
        PurchaseOrder purchaseOrder = purchaseOrderDb.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order tidak ditemukan"));

        // Validasi tahapan purchase order harus "CREATED"
        if (!purchaseOrder.getStatus().equalsIgnoreCase("CREATED")) {
            throw new IllegalArgumentException("Purchase Order harus dalam status CREATED untuk melakukan konfirmasi");
        }

        // Validasi untuk tanggal invoice tidak boleh sebelum tanggal pembelian dan tidak boleh di masa lalu
        LocalDate invoiceDate = request.getInvoiceDate(); // ini input tanggal sendiri dari frontend
        LocalDate purchaseDate = purchaseOrder.getPurchaseDate();

        if (invoiceDate.isBefore(purchaseDate) || invoiceDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal invoice tidak boleh sebelum tanggal pembelian");
        }

        PurchaseInvoice invoice = new PurchaseInvoice();
        invoice.setId(generateInvoiceId());
        invoice.setInvoiceDate(invoiceDate);
        invoice.setInvoiceStatus(InvoiceStatus.ISSUED); // ISSUED, PAID (PAS UDAH BAYAR BARU UBAH KE INI)
        invoice.setTotalAmount(purchaseOrder.getTotalPrice());
        invoice.setPurchaseOrder(purchaseOrder);
        invoice.setPaymentTerms(request.getPaymentTerms());
        invoice.setDueDate(invoiceDate.plusDays(request.getPaymentTerms())); // Jatuh tempo invoice = tanggal invoice + payment terms
        purchaseOrder.setInvoice(invoice);

        purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);

        // TODO: tambahin attribute di invoice sesuai dengan kebutuhan, utk ricky, ubah juga di dto request (PurchaseOrderInvoiceRequestDTO) 
        // dan response (PurchaseInvoiceResponseDTO) sama model (PurchaseInvoice)
        
        return purchaseOrderToPurchaseOrderResponseDTO(purchaseOrderDb.save(purchaseOrder));
    }

    // STAGE 3: PAID
    @Override
    public PurchaseOrderResponseDTO payPurchaseOrder(String id, PurchasePaymentRequestDTO request) {
        // Validasi untuk purchase order ada atau tidak
        PurchaseOrder purchaseOrder = purchaseOrderDb.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order tidak ditemukan"));

        // Validasi tahapan purchase order harus "CONFIRMED"
        if (!purchaseOrder.getStatus().equalsIgnoreCase("CONFIRMED")) {
            throw new IllegalArgumentException("Purchase Order harus dalam status CONFIRMED untuk melakukan pembayaran");
        }
  
        // Validasi untuk tanggal pembayaran tidak boleh sebelum tanggal pembelian atau tidak boleh di masa lalu atau tidak boleh sebelum tanggal invoice
        LocalDate purchaseDate = purchaseOrder.getPurchaseDate();
        LocalDate invoiceDate = purchaseOrder.getInvoice().getInvoiceDate();
        LocalDate paymentDate = request.getPaymentDate(); // ini input tanggal sendiri dari frontend

        if (paymentDate.isBefore(purchaseDate) || paymentDate.isBefore(LocalDate.now()) || paymentDate.isBefore(invoiceDate)) {
            throw new IllegalArgumentException("Tanggal pembayaran tidak boleh sebelum tanggal pembelian dan tanggal tagihan");
        }

        PurchasePayment payment = purchaseOrder.getPayment();

        if (payment == null) {
            payment = new PurchasePayment();
            payment.setId(generatePaymentId(purchaseOrder.getCustomer().getName()));
            payment.setPurchaseOrder(purchaseOrder);
            payment.setTotalAmountPayed(BigDecimal.ZERO);
        }

        payment.setPaymentDate(paymentDate);
        payment.setPaymentMethod(request.getPaymentMethod()); // kalo udah partially paid, ini prefilled dari yang udah ada (frontend), 
                                                                // jadi payment methodnya sama terus
 
        BigDecimal additionalPaid = request.getTotalAmountPayed();
        BigDecimal totalPrice = purchaseOrder.getTotalPrice();
        BigDecimal currentTotalPaid = payment.getTotalAmountPayed();

        BigDecimal newTotalPaid = currentTotalPaid.add(additionalPaid);

        if (newTotalPaid.compareTo(totalPrice) > 0) {
            throw new IllegalArgumentException("Jumlah pembayaran melebihi total harga");
        } else if (newTotalPaid.compareTo(totalPrice) == 0) {
            payment.setPaymentStatus(PaymentStatus.PAID);
            purchaseOrder.setStatus(PurchaseOrderStatus.PAID);
            purchaseOrder.getInvoice().setInvoiceStatus(InvoiceStatus.PAID);
        } else {
            payment.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
            purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);
            purchaseOrder.getInvoice().setInvoiceStatus(InvoiceStatus.ISSUED);
        }

        payment.setTotalAmountPayed(newTotalPaid);
        purchaseOrder.setPayment(payment);
       

        return purchaseOrderToPurchaseOrderResponseDTO(purchaseOrderDb.save(purchaseOrder));
    }

    // STAGE 4: IN DELIVERY, SURAT JALAN (JESS)
    @Override
    public PurchaseOrderResponseDTO deliverPurchaseOrder(String id, DeliveryRequestDTO request) {
        // Validasi untuk purchase order ada atau tidak
        PurchaseOrder purchaseOrder = purchaseOrderDb.findById(id)
            .orElseThrow(() -> new RuntimeException("Purchase Order tidak ditemukan"));

        // Validasi tahapan purchase order harus "PAID" dan invoice harus "PAID"
        if (!purchaseOrder.getStatus().equalsIgnoreCase("PAID")) {
            throw new IllegalArgumentException("Purchase Order harus dalam status PAID untuk melakukan pembayaran");
        }

        LocalDate purchaseDate = purchaseOrder.getPurchaseDate();
        LocalDate invoiceDate = purchaseOrder.getInvoice().getInvoiceDate();

        // Validasi untuk tanggal pengiriman tidak boleh sebelum tanggal pembelian atau tidak boleh di masa lalu
        LocalDate deliveryDate = request.getDeliveryDate();
        if (deliveryDate.isBefore(purchaseDate) || deliveryDate.isBefore(LocalDate.now()) || deliveryDate.isBefore(invoiceDate)) {
            throw new IllegalArgumentException("Tanggal pengiriman tidak boleh sebelum tanggal pembelian dan tanggal tagihan");
        }

        Delivery delivery = new Delivery();
        delivery.setId(generateDeliveryId(purchaseOrder.getItems()));
        delivery.setDeliveryDate(deliveryDate);
        delivery.setDeliveryStatus(DeliveryStatus.IN_DELIVERY); // IN DELIVERY, DELIVERED (KETIKA UDAH SAMPAI NANTI TAHAP SELANJUTNYA)
        delivery.setTrackingNumber(generateTrackingNumber(purchaseOrder.getItems()));
        delivery.setDeliveryFee(request.getDeliveryFee());
        purchaseOrder.setStatus(PurchaseOrderStatus.IN_DELIVERY);
        delivery.setPurchaseOrder(purchaseOrder);
        purchaseOrder.setDelivery(delivery);

        // TODO: tambahin attribute di delivery sesuai dengan kebutuhan, utk jess, ubah juga di dto request (DeliveryRequestDTO) 
        // dan response (DeliveryResponseDTO) sama model (Delivery)

        return purchaseOrderToPurchaseOrderResponseDTO(purchaseOrderDb.save(purchaseOrder));
    }

    // STAGE 5: COMPLETED, NOTA (MICHAEL)
    @Override
    public PurchaseOrderResponseDTO completePurchaseOrder(String id) {
        // Validasi untuk purchase order ada atau tidak
        PurchaseOrder purchaseOrder = purchaseOrderDb.findById(id)
            .orElseThrow(() -> new RuntimeException("Purchase Order tidak ditemukan"));

        // Validasi tahapan purchase order harus "IN DELIVERY" dan delivery status harus "IN DELIVERY"
        if (!purchaseOrder.getStatus().equalsIgnoreCase("IN DELIVERY")) {
            throw new IllegalArgumentException("Purchase Order harus dalam status IN DELIVERY untuk melakukan pembayaran");
        }

        PurchaseReceipt receipt = new PurchaseReceipt();
        receipt.setId(generateReceiptId(purchaseOrder.getItems()));
        receipt.setReceiptDate(purchaseOrder.getPayment().getPaymentDate()); // tanggal nota = tanggal pembayaran
        receipt.setAmountPayed(purchaseOrder.getPayment().getTotalAmountPayed());
        purchaseOrder.setStatus(PurchaseOrderStatus.COMPLETED);
        receipt.setPurchaseOrder(purchaseOrder);
        purchaseOrder.setReceipt(receipt);
        purchaseOrder.getDelivery().setDeliveryStatus(DeliveryStatus.DELIVERED); // update status delivery jadi delivered

        // TODO: tambahin attribute di purchase receipt sesuai dengan kebutuhan, utk michael, ubah juga di response (PurchaseReceiptResponseDTO) sama model (PurchaseReceipt)

        // Update stock barang di gudang tujuan sesuai dengan quantity yang diterima
        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            Barang barang = item.getBarang();
            for(StockBarangPerGudang stock : barang.getListStockBarang()){
                if(stock.getGudang().getNama().equals(item.getGudangTujuan().getNama())){
                    stock.setStock(stock.getStock() + item.getQuantity()); // update stock barang di gudang tujuan
                }
            }
        }

        return purchaseOrderToPurchaseOrderResponseDTO(purchaseOrderDb.save(purchaseOrder));
    }
}
