package propensi.amesta.service.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import propensi.amesta.model.Customer;
import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.Purchase.Delivery;
import propensi.amesta.model.Purchase.PurchaseInvoice;
import propensi.amesta.model.Purchase.PurchaseOrder;
import propensi.amesta.model.Purchase.PurchaseOrderItem;
import propensi.amesta.model.Purchase.PurchasePayment;
import propensi.amesta.model.Purchase.PurchaseReceipt;
import propensi.amesta.payload.request.Purchase.PurchaseOrderInvoiceRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchaseOrderItemRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchaseOrderRequestDTO;
import propensi.amesta.payload.response.Purchase.DeliveryResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchaseInvoiceResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchaseOrderItemResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchaseOrderResponseDTO;
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
        // LocalDate invoiceDate = request.getInvoiceDate();
        // if (invoiceDate.isBefore(purchaseDate) || invoiceDate.isBefore(LocalDate.now())) {
        //     throw new IllegalArgumentException("Tanggal invoice tidak boleh sebelum tanggal pembelian");
        // }

        // Validasi untuk tanggal pengiriman tidak boleh sebelum tanggal pembelian atau tidak boleh di masa lalu
        // LocalDate deliveryDate = request.getDelivery().getDeliveryDate();
        // if (deliveryDate.isBefore(purchaseDate) || deliveryDate.isBefore(LocalDate.now())) {
        //     throw new IllegalArgumentException("Tanggal pengiriman tidak boleh sebelum tanggal pembelian");
        // }

        // Validasi untuk tanggal pembayaran tidak boleh sebelum tanggal pembelian atau tidak boleh di masa lalu
        // LocalDate paymentDate = request.getPayment().getPaymentDate();
        // if (paymentDate.isBefore(purchaseDate) || paymentDate.isBefore(LocalDate.now()) || paymentDate.isBefore(invoiceDate)) {
        //     throw new IllegalArgumentException("Tanggal pembayaran tidak boleh sebelum tanggal pembelian");
        // }

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
        purchaseOrder.setStatus("CREATED");
        purchaseOrder.setPurchaseDate(request.getPurchaseDate());

        BigDecimal total = BigDecimal.ZERO;
        List<PurchaseOrderItem> items = new ArrayList<>();
        for (PurchaseOrderItemRequestDTO itemDTO : request.getItems()) {
                Barang barang = barangDb.findById(itemDTO.getBarangId())
                        .orElseThrow(() -> new IllegalArgumentException("Barang dengan ID " + itemDTO.getBarangId() + " tidak ditemukan"));

                BigDecimal unitPrice = barang.getHarga();
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
                item.setGudangTujuan(gudangDb.findById(itemDTO.getGudangTujuan()) // gaperlu dari dto, bisa juga dari barangnya langsung (asumsi gamau bisa ganti gudang tujuan di po)
                        .orElseThrow(() -> new IllegalArgumentException("Gudang tujuan dengan ID " + itemDTO.getGudangTujuan() + " tidak ditemukan")));

                items.add(item);
        }

        purchaseOrder.setItems(items);
        purchaseOrder.setTotalPrice(total);

        // PurchasePayment payment = new PurchasePayment();
        // payment.setId(generatePaymentId(customer.getName()));
        // payment.setPaymentDate(paymentDate);
        // payment.setTotalAmountPayed(BigDecimal.valueOf(0)); // Belum ada total amount payed di tahap ini, karena belum ada pembayaran
        // payment.setPaymentMethod(request.getPayment().getPaymentMethod());
        // payment.setPaymentStatus("UNPAID"); // UNPAID, PAID, REFUNDED,
        // payment.setPurchaseOrder(purchaseOrder);
        // purchaseOrder.setPayment(payment);

        // Delivery delivery = new Delivery();
        // delivery.setId(generateDeliveryId(items)); // ID = UUID
        // delivery.setDeliveryDate(deliveryDate);
        // delivery.setDeliveryStatus("PENDING"); // PENDING, IN_PROGRESS, DELIVERED, CANCELLED
        // delivery.setTrackingNumber(generateTrackingNumber(items));
        // delivery.setDeliveryFee(request.getDelivery().getDeliveryFee());
        // delivery.setPurchaseOrder(purchaseOrder);
        // purchaseOrder.setDelivery(delivery);

        // PurchaseInvoice invoice = new PurchaseInvoice();
        // invoice.setId(generateInvoiceId());
        // invoice.setInvoiceDate(invoiceDate);
        // invoice.setInvoiceStatus("DRAFT"); // DRAFT,ISSUED, PAID
        // invoice.setTotalAmount(total);
        // invoice.setPurchaseOrder(purchaseOrder);
        // purchaseOrder.setInvoice(invoice);

        // TODO: PurchaseReceipt (nota pembelian) belum dibuat di tahap ini, dibuat saat sudah update status ke tahap selanjutnya, (untuk Michael :D)
        // TODO: Faktur pembelian (modelnya belom dibuat) juga belum dibuat di tahap ini, dibuat saat sudah update status ke tahap selanjutnya (setelah nota pembelian), (untuk Ricky :D)
        // TODO: integrasi dengan model barang nantinya untuk tahap selanjutnya (setelah semua selesai dan barang diterima), tambahin stock ke existing stock barang, correspond dengan gudangnya juga sesuai dengan PO.
        // TODO: implementasi security di websecurityconfig.

        return purchaseOrderToPurchaseOrderResponseDTO(purchaseOrderDb.save(purchaseOrder));
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
                invoice,
                delivery,
                payment,
                purchaseOrder.getStatus(),
                items,
                receipt
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

    private PurchaseReceiptResponseDTO purchaseReceiptToPurchaseReceiptResponseDTO(PurchaseReceipt purchaseReceipt) {
        return new PurchaseReceiptResponseDTO(
                purchaseReceipt.getId(),
                purchaseReceipt.getPurchaseOrder().getId(),
                purchaseReceipt.getReceiptDate(),
                purchaseReceipt.getAmountPayed()
        );
    }
    
    public String generateTrackingNumber(List<PurchaseOrderItem> items) {
        // Format: UUID-XXX-Y, di mana UUID adalah 6 karakter acak, XXX adalah 3 huruf pertama dari nama barang, dan Y adalah 1 digit angka 
        String uuidPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        StringBuilder trackingNumber = new StringBuilder(uuidPart);

        String y = String.valueOf(items.size());

        trackingNumber.append("-").append(items.get(0).getBarang().getNama().substring(0, 3).toUpperCase()).append(y);

        return trackingNumber.toString();
    }

    public String generatePoId(){
        // Format: PO-YYYY-MM-DD-XXXXX, di mana YYYY adalah tahun, MM adalah bulan dalam angka romawi, DD adalah tanggal, dan XXXXX adalah 5 karakter acak
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

    public String generatePaymentId(String customerName) {
        // Format: PAY-XXX-MM-YY-XXXXX, di mana XXX adalah 3 huruf pertama dari nama customer, MM adalah bulan dalam angka romawi, YY adalah tahun dalam 2 digit, dan XXXXX adalah 5 karakter acak
        String prefix = "PAY-";
    
        String kodeCustomer = customerName.replace(" ", "").length() >= 3
            ? customerName.replace(" ", "").substring(0, 3).toUpperCase()
            : customerName.replace(" ", "").toUpperCase();
    
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

    @Override
    public List<PurchaseOrderResponseDTO> getAllPurchaseOrders() {
        List<PurchaseOrder> purchaseOrders = purchaseOrderDb.findAll();
        List<PurchaseOrderResponseDTO> purchaseOrderResponseDTOs = new ArrayList<>();

        for (PurchaseOrder purchaseOrder : purchaseOrders) {
            PurchaseOrderResponseDTO purchaseOrderResponseDTO = purchaseOrderToPurchaseOrderResponseDTO(purchaseOrder);
            purchaseOrderResponseDTOs.add(purchaseOrderResponseDTO);
        }

        return purchaseOrderResponseDTOs;
    }

    @Override
    public PurchaseOrderResponseDTO getPurchaseOrderById(String id) {
        PurchaseOrder purchaseOrder = purchaseOrderDb.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order tidak ditemukan"));

        return purchaseOrderToPurchaseOrderResponseDTO(purchaseOrder);
    }

    // Stages: 
    // 1. CREATED = PO baru saja dibuat, menunggu konfirmasi dari vendor
    // 2. CONFIRMED = PO sudah dikonfirmasi oleh vendor, menunggu pembayaran dari pembeli, muncul faktur
    // 3. PAID = Pembeli sudah melakukan pembayaran, menunggu pengiriman dari vendor, tahap masukin pembayaran
    // 4. DELIVERED = Barang sudah diterima oleh pembeli, menunggu nota pembelian dari vendor, muncul surat jalan
    // 6. COMPLETED = Nota sudah diterima oleh pembeli, PO selesai
    
    // STAGE 2: CONFIRMED
    @Override
    public PurchaseOrderResponseDTO confirmPurchaseOrder(String id, PurchaseOrderInvoiceRequestDTO request) {
        // Validasi untuk purchase order ada atau tidak
        PurchaseOrder purchaseOrder = purchaseOrderDb.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order tidak ditemukan"));

        LocalDate purchaseDate = purchaseOrder.getPurchaseDate();

        // Validasi untuk tanggal invoice tidak boleh sebelum tanggal pembelian atau tidak boleh di masa lalu
        LocalDate invoiceDate = request.getInvoiceDate();
        if (invoiceDate.isBefore(purchaseDate) || invoiceDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal invoice tidak boleh sebelum tanggal pembelian");
        }

        PurchaseInvoice invoice = new PurchaseInvoice();
        invoice.setId(generateInvoiceId());
        invoice.setInvoiceDate(invoiceDate);
        invoice.setInvoiceStatus("ISSUED"); // ISSUED, PAID (PAS UDAH BAYAR BARU UBAH KE INI)
        invoice.setTotalAmount(purchaseOrder.getTotalPrice());
        invoice.setPurchaseOrder(purchaseOrder);
        purchaseOrder.setInvoice(invoice);

        purchaseOrder.setStatus("CONFIRMED");
        
        return purchaseOrderToPurchaseOrderResponseDTO(purchaseOrderDb.save(purchaseOrder));
    }
    
}