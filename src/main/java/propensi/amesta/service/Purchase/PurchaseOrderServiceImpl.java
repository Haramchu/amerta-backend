package propensi.amesta.service.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import propensi.amesta.payload.response.Purchase.PurchaseOrderItemResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchaseOrderResponseDTO;
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
            throw new IllegalArgumentException("Customer bukan merupakan vendor");
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
        LocalDate deliveryDate = request.getDeliveryDate();
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
                item.setGudangTujuan(gudangDb.findById(itemDTO.getGudangTujuan()) // gaperlu dari dto, bisa juga dari barangnya langsung (asumsi gamau bisa ganti gudang tujuan di po)
                        .orElseThrow(() -> new IllegalArgumentException("Gudang tujuan dengan ID " + itemDTO.getGudangTujuan() + " tidak ditemukan")));

                items.add(item);
        }

        purchaseOrder.setItems(items);
        purchaseOrder.setTotalPrice(total);

        PurchasePayment payment = new PurchasePayment();
        payment.setId(UUID.randomUUID());
        payment.setPaymentDate(paymentDate);
        payment.setPaymentMethod(request.getPayment().getPaymentMethod());
        payment.setPaymentStatus("UNPAID");
        payment.setPurchaseOrder(purchaseOrder);
        purchaseOrder.setPayment(payment);

        Delivery delivery = new Delivery();
        delivery.setId(generateDeliveryId(items)); // ID = UUID
        delivery.setDeliveryDate(deliveryDate);
        delivery.setDeliveryStatus("PENDING");
        delivery.setTrackingNumber(generateTrackingNumber(items));
        delivery.setPurchaseOrder(purchaseOrder);
        purchaseOrder.setDelivery(delivery);

        PurchaseInvoice invoice = new PurchaseInvoice();
        invoice.setId(generateInvoiceId());
        invoice.setInvoiceDate(invoiceDate);
        invoice.setAmount(total);
        invoice.setPurchaseOrder(purchaseOrder);
        purchaseOrder.setInvoice(invoice);

        // TODO: PurchaseReceipt (nota pembelian) belum dibuat di tahap ini, dibuat saat sudah update status ke tahap selanjutnya, (untuk Michael :D)
        // TODO: Faktur pembelian (modelnya belom dibuat) juga belum dibuat di tahap ini, dibuat saat sudah update status ke tahap selanjutnya (setelah nota pembelian), (untuk Ricky :D)
        // TODO: integrasi dengan model barang nantinya untuk tahap selanjutnya (setelah semua selesai dan barang diterima), tambahin stock ke existing stock barang, correspond dengan gudangnya juga sesuai dengan PO.
        // TODO: implementasi security di websecurityconfig.

        return purchaseOrderToPurchaseOrderResponseDTO(purchaseOrderDb.save(purchaseOrder));
    }

    private PurchaseOrderResponseDTO purchaseOrderToPurchaseOrderResponseDTO(PurchaseOrder purchaseOrder) {
        PurchasePaymentResponseDTO payment = purchasePaymentToPurchasePaymentResponseDTO(purchaseOrder.getPayment());

        List<PurchaseOrderItemResponseDTO> items = new ArrayList<>();
        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            items.add(purchaseOrderItemToPurchaseOrderItemResponseDTO(item));
        }

        return new PurchaseOrderResponseDTO(
                purchaseOrder.getId(),
                purchaseOrder.getCustomer().getId(),
                purchaseOrder.getPurchaseDate(),
                purchaseOrder.getInvoice().getInvoiceDate(),
                purchaseOrder.getDelivery().getDeliveryDate(),
                purchaseOrder.getTotalPrice(),
                payment,
                purchaseOrder.getStatus(),
                items
        );
    }

    private PurchasePaymentResponseDTO purchasePaymentToPurchasePaymentResponseDTO(PurchasePayment purchasePayment) {
        return new PurchasePaymentResponseDTO(
                purchasePayment.getPaymentDate(),
                purchasePayment.getPaymentMethod()
        );
    }

    private PurchaseOrderItemResponseDTO purchaseOrderItemToPurchaseOrderItemResponseDTO(PurchaseOrderItem purchaseOrderItem) {
        return new PurchaseOrderItemResponseDTO(
                purchaseOrderItem.getGudangTujuan().getNama(),
                purchaseOrderItem.getBarang().getId(),
                purchaseOrderItem.getQuantity()
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

    public String generateDeliveryId(List<PurchaseOrderItem> items) {
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
    
}
