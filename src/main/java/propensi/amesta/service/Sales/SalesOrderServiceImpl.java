package propensi.amesta.service.Sales;

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
import propensi.amesta.model.Aset.StockBarangPerGudang;
import propensi.amesta.model.Sales.Shipping;
import propensi.amesta.model.Sales.SalesInvoice;
import propensi.amesta.model.Sales.SalesOrder;
import propensi.amesta.model.Sales.SalesOrderItem;
import propensi.amesta.model.Sales.SalesPayment;
import propensi.amesta.model.Sales.SalesReceipt;
import propensi.amesta.payload.request.Sales.ShippingRequestDTO;
import propensi.amesta.payload.request.Sales.SalesOrderInvoiceRequestDTO;
import propensi.amesta.payload.request.Sales.SalesOrderItemRequestDTO;
import propensi.amesta.payload.request.Sales.SalesOrderRequestDTO;
import propensi.amesta.payload.request.Sales.SalesPaymentRequestDTO;
import propensi.amesta.payload.response.Sales.ShippingResponseDTO;
import propensi.amesta.payload.response.Sales.SalesInvoiceResponseDTO;
import propensi.amesta.payload.response.Sales.SalesOrderItemResponseDTO;
import propensi.amesta.payload.response.Sales.SalesOrderResponseDTO;
import propensi.amesta.payload.response.Sales.SalesPaymentResponseDTO;
import propensi.amesta.payload.response.Sales.SalesReceiptResponseDTO;
import propensi.amesta.repository.CustomerDb;
import propensi.amesta.repository.Aset.BarangDb;
import propensi.amesta.repository.Aset.GudangDb;
import propensi.amesta.repository.Sales.SalesOrderDb;

@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    @Autowired
    private CustomerDb customerDb;

    @Autowired
    private BarangDb barangDb;

    @Autowired
    private SalesOrderDb salesOrderDb;

    @Autowired
    private GudangDb gudangDb;


    // STAGE 1: CREATED
    @Override
    public SalesOrderResponseDTO addSalesOrder(SalesOrderRequestDTO request) {
        // Validasi customer ada atau tidak, jika ada cek apakah vendor atau tidak
         Customer customer = customerDb.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer tidak ditemukan"));

        if (!customer.getRole().equalsIgnoreCase("VENDOR")){
            throw new IllegalArgumentException("Customer harus merupakan vendor");
        }
        
        // Validasi untuk tanggal pembelian tidak boleh di masa lalu
        LocalDate salesDate = request.getSalesDate();
        if (salesDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal pembelian tidak boleh di masa lalu");
        }

        // Validasi untuk barang harus ada dan kuantitas tidak boleh negatif
        for (SalesOrderItemRequestDTO item : request.getItems()) {
                if (!barangDb.existsById(item.getBarangId())) {
                        throw new IllegalArgumentException("Barang dengan ID " + item.getBarangId() + " tidak ditemukan");
                }

                if (item.getQuantity() <= 0) {
                        throw new IllegalArgumentException("Kuantitas barang tidak boleh negatif atau nol");
                }
        }

        // Validasi multiple entry, jika barang dan gudang tujuan sama di SalesOrderItem, kuantitas dijumlahkan, validasi pajaknya juga untuk barang yang dan gudang tujuan sama, pajak juga harus sama
        Map<String, SalesOrderItemRequestDTO> itemMap = new HashMap<>();

        for (SalesOrderItemRequestDTO item : request.getItems()) {
            String key = item.getBarangId() + "|" + item.getGudangTujuan();

            if (itemMap.containsKey(key)) {
                SalesOrderItemRequestDTO existing = itemMap.get(key);

                // Validasi pajak harus sama
                if (!existing.getPajak().equals(item.getPajak())) {
                    throw new IllegalArgumentException("Barang dengan ID " + item.getBarangId() + " dan gudang tujuan " + item.getGudangTujuan() + " memiliki pajak yang berbeda");
                }

                // Kalau pajak sama, jumlahkan quantity
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
            } else {
                itemMap.put(key, new SalesOrderItemRequestDTO(
                    item.getBarangId(),
                    item.getQuantity(),
                    item.getGudangTujuan(),
                    item.getPajak()
                ));
            }
        }

        List<SalesOrderItemRequestDTO> consolidatedItems = new ArrayList<>(itemMap.values());
        request.setItems(consolidatedItems);


        // Validasi gudang tujuan harus ada
        for (SalesOrderItemRequestDTO item : request.getItems()) {
            gudangDb.findById(item.getGudangTujuan())
                    .orElseThrow(() -> new IllegalArgumentException("Gudang tujuan dengan ID " + item.getGudangTujuan() + " tidak ditemukan"));
        }

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(generatePoId());
        salesOrder.setCustomer(customer);
        salesOrder.setStatus("CREATED");
        salesOrder.setSalesDate(request.getSalesDate());

        BigDecimal total = BigDecimal.ZERO;
        List<SalesOrderItem> items = new ArrayList<>();
        for (SalesOrderItemRequestDTO itemDTO : request.getItems()) {
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

                SalesOrderItem item = new SalesOrderItem();
                item.setId(UUID.randomUUID()); // ID = UUID
                item.setSalesOrder(salesOrder);
                item.setBarang(barang);
                item.setQuantity(itemDTO.getQuantity());
                item.setTax(pajak);
                item.setGudangTujuan(gudangDb.findById(itemDTO.getGudangTujuan()) // gaperlu dari dto, bisa juga dari barangnya langsung (asumsi gamau bisa ganti gudang tujuan di po)
                        .orElseThrow(() -> new IllegalArgumentException("Gudang tujuan dengan ID " + itemDTO.getGudangTujuan() + " tidak ditemukan")));

                items.add(item);
        }

        salesOrder.setItems(items);
        salesOrder.setTotalPrice(total);

        // TODO: implementasi security di websecurityconfig!!!!!!!!!!!

        return salesOrderToSalesOrderResponseDTO(salesOrderDb.save(salesOrder));
    }

    private SalesOrderResponseDTO salesOrderToSalesOrderResponseDTO(SalesOrder salesOrder) {
        SalesPaymentResponseDTO payment = null;
        SalesInvoiceResponseDTO invoice = null;
        ShippingResponseDTO shipping = null;
        SalesReceiptResponseDTO receipt = null;

        if(salesOrder.getPayment() != null) {
            payment = salesPaymentToSalesPaymentResponseDTO(salesOrder.getPayment());
        }

        if(salesOrder.getInvoice() != null) {
            invoice = salesInvoiceToSalesInvoiceResponseDTO(salesOrder.getInvoice());
        }

        if(salesOrder.getShipping() != null) {
            shipping = shippingToShippingResponseDTO(salesOrder.getShipping());
        }

        if(salesOrder.getReceipt() != null) {
            receipt = salesReceiptToSalesReceiptResponseDTO(salesOrder.getReceipt());
        }

        List<SalesOrderItemResponseDTO> items = new ArrayList<>();
        for (SalesOrderItem item : salesOrder.getItems()) {
            items.add(salesOrderItemToSalesOrderItemResponseDTO(item));
        }
        
        return new SalesOrderResponseDTO(
                salesOrder.getId(),
                salesOrder.getCustomer().getId(),
                salesOrder.getSalesDate(),
                salesOrder.getStatus(),
                items,
                salesOrder.getTotalPrice(),
                invoice,
                shipping,
                receipt,
                payment
        );
       
    }

    private SalesPaymentResponseDTO salesPaymentToSalesPaymentResponseDTO(SalesPayment salesPayment) {
        return new SalesPaymentResponseDTO(
                salesPayment.getId(),
                salesPayment.getSalesOrder().getId(),
                salesPayment.getPaymentDate(),
                salesPayment.getPaymentMethod(),
                salesPayment.getPaymentStatus(),
                salesPayment.getTotalAmountPayed()
        );
    }

    private SalesOrderItemResponseDTO salesOrderItemToSalesOrderItemResponseDTO(SalesOrderItem salesOrderItem) {
        return new SalesOrderItemResponseDTO(
                salesOrderItem.getId(),
                salesOrderItem.getSalesOrder().getId(),
                salesOrderItem.getBarang().getId(),
                salesOrderItem.getQuantity(),
                salesOrderItem.getGudangTujuan().getNama()

        );
    }

    private ShippingResponseDTO shippingToShippingResponseDTO(Shipping shipping) {
        return new ShippingResponseDTO(
                shipping.getId(),
                shipping.getSalesOrder().getId(),
                shipping.getShippingDate(),
                shipping.getShippingStatus(),
                shipping.getTrackingNumber(),
                shipping.getShippingFee()
        );
    }

    private SalesInvoiceResponseDTO salesInvoiceToSalesInvoiceResponseDTO(SalesInvoice salesInvoice) {
        BigDecimal remainingAmount = salesInvoice.getTotalAmount();

        if (salesInvoice.getSalesOrder().getPayment() != null) {
            remainingAmount = salesInvoice.getTotalAmount().subtract(salesInvoice.getSalesOrder().getPayment().getTotalAmountPayed());
        } else {
            remainingAmount = salesInvoice.getTotalAmount();
        }

        return new SalesInvoiceResponseDTO(
                salesInvoice.getId(),
                salesInvoice.getSalesOrder().getId(),
                salesInvoice.getInvoiceDate(),
                salesInvoice.getInvoiceStatus(),
                salesInvoice.getTotalAmount(),
                salesInvoice.getPaymentTerms(),
                salesInvoice.getDueDate(),
                remainingAmount
        );
    }

    private SalesReceiptResponseDTO salesReceiptToSalesReceiptResponseDTO(SalesReceipt salesReceipt) {
        return new SalesReceiptResponseDTO(
                salesReceipt.getId(),
                salesReceipt.getSalesOrder().getId(),
                salesReceipt.getReceiptDate(),
                salesReceipt.getAmountReceived()
        );
    }
    
    public String generateTrackingNumber(List<SalesOrderItem> items) {
        // Format: UUID-XXX-Y, di mana UUID adalah 6 karakter acak, XXX adalah 3 huruf pertama dari nama barang, dan Y adalah 1 digit angka 
        String uuidPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        StringBuilder trackingNumber = new StringBuilder(uuidPart);

        String y = String.valueOf(items.size());

        trackingNumber.append("-").append(items.get(0).getBarang().getNama().replace(" ", "").substring(0, 3).toUpperCase()).append(y);

        return trackingNumber.toString();
    }

    public String generatePoId(){
        // Format: PO-YYYY-MM-DD-XXXXX, di mana YYYY adalah tahun, MM adalah bulan dalam angka romawi, DD adalah tanggal, dan XXXXX adalah 5 karakter acak
        String id = "SO-";
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
        String id = "INVS-";
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

    public String generateShippingId(List<SalesOrderItem> items) {
        // Format: SHP-XXX-DD-MM-XXXXX, di mana XXX adalah 3 huruf pertama dari nama barang, DD adalah tanggal dalam angka romawi, MM adalah bulan dalam angka romawi, dan XXXXX adalah 5 karakter acak
        String prefix = "SHP-";
    
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
        // Format: RCV-XXX-MM-YY-XXXXX, di mana XXX adalah 3 huruf pertama dari nama customer, MM adalah bulan dalam angka romawi, YY adalah tahun dalam 2 digit, dan XXXXX adalah 5 karakter acak
        String prefix = "RCV-";
    
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

    public String generateReceiptId(List<SalesOrderItem> items) {
        // Format: RECS-XXX-DDMMYY-XXXXX
    
        String prefix = "RECS-";
    
        String namaBarang = items.get(0).getBarang().getNama().replace(" ", "");
        String kodeBarang = namaBarang.length() >= 3
                ? namaBarang.substring(0, 3).toUpperCase()
                : namaBarang.toUpperCase();
    
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyy"));
    
        String randomPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5).toUpperCase();
    
        return prefix + kodeBarang + "-" + datePart + "-" + randomPart;
    }
    

    @Override
    public List<SalesOrderResponseDTO> getAllSalesOrders() {
        List<SalesOrder> salesOrders = salesOrderDb.findAll();
        List<SalesOrderResponseDTO> salesOrderResponseDTOs = new ArrayList<>();

        for (SalesOrder salesOrder : salesOrders) {
            SalesOrderResponseDTO salesOrderResponseDTO = salesOrderToSalesOrderResponseDTO(salesOrder);
            salesOrderResponseDTOs.add(salesOrderResponseDTO);
        }

        return salesOrderResponseDTOs;
    }

    @Override
    public SalesOrderResponseDTO getSalesOrderById(String id) {
        SalesOrder salesOrder = salesOrderDb.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales Order tidak ditemukan"));

        return salesOrderToSalesOrderResponseDTO(salesOrder);
    }

    // Stages: 
    // 1. CREATED = PO baru saja dibuat, menunggu konfirmasi dari vendor
    // 2. CONFIRMED = PO sudah dikonfirmasi oleh vendor, menunggu pembayaran dari pembeli, muncul faktur
    // 3. PAID = Pembeli sudah melakukan pembayaran, menunggu pengiriman dari vendor, tahap masukin pembayaran
    // 4. IN DELIVERY = Barang sedang dikirim oleh pembeli, menunggu nota pembelian dari vendor, muncul surat jalan
    // 5. COMPLETED = Nota sudah diterima oleh pembeli, SO selesai
    
    // STAGE 2: CONFIRMED, FAKTUR (RICKY)
    @Override
    public SalesOrderResponseDTO confirmSalesOrder(String id, SalesOrderInvoiceRequestDTO request) {
        // Validasi untuk sales order ada atau tidak
        SalesOrder salesOrder = salesOrderDb.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales Order tidak ditemukan"));

        // Validasi tahapan sales order harus "CREATED"
        if (!salesOrder.getStatus().equalsIgnoreCase("CREATED")) {
            throw new IllegalArgumentException("sales Order harus dalam status CREATED untuk melakukan konfirmasi");
        }

        // Validasi untuk tanggal invoice tidak boleh sebelum tanggal penjualan dan tidak boleh di masa lalu
        LocalDate invoiceDate = request.getInvoiceDate(); // ini input tanggal sendiri dari frontend
        LocalDate salesDate = salesOrder.getSalesDate();

        if (invoiceDate.isBefore(salesDate) || invoiceDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal invoice tidak boleh sebelum tanggal penjualan");
        }

        SalesInvoice invoice = new SalesInvoice();
        invoice.setId(generateInvoiceId());
        invoice.setInvoiceDate(invoiceDate);
        invoice.setInvoiceStatus("ISSUED"); // ISSUED, PAID (PAS UDAH BAYAR BARU UBAH KE INI)
        invoice.setTotalAmount(salesOrder.getTotalPrice());
        invoice.setSalesOrder(salesOrder);
        invoice.setPaymentTerms(request.getPaymentTerms());
        invoice.setDueDate(invoiceDate.plusDays(request.getPaymentTerms())); // Jatuh tempo invoice = tanggal invoice + payment terms
        salesOrder.setInvoice(invoice);

        salesOrder.setStatus("CONFIRMED");

        // TODO: tambahin attribute di invoice sesuai dengan kebutuhan, utk ricky, ubah juga di dto request (SalesOrderInvoiceRequestDTO) 
        // dan response (SalesInvoiceResponseDTO) sama model (SalesInvoice)
        
        return salesOrderToSalesOrderResponseDTO(salesOrderDb.save(salesOrder));
    }

    // STAGE 3: PAID
    @Override
    public SalesOrderResponseDTO paySalesOrder(String id, SalesPaymentRequestDTO request) { 
        // Validasi untuk sales order ada atau tidak
        SalesOrder salesOrder = salesOrderDb.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales Order tidak ditemukan"));

        // Validasi tahapan sales order harus "CONFIRMED"
        if (!salesOrder.getStatus().equalsIgnoreCase("CONFIRMED")) {
            throw new IllegalArgumentException("Sales Order harus dalam status CONFIRMED untuk melakukan pembayaran");
        }
  
        // Validasi untuk tanggal pembayaran tidak boleh sebelum tanggal penjualan atau tidak boleh di masa lalu atau tidak boleh sebelum tanggal invoice
        LocalDate salesDate = salesOrder.getSalesDate();
        LocalDate invoiceDate = salesOrder.getInvoice().getInvoiceDate();
        LocalDate paymentDate = request.getPaymentDate(); // ini input tanggal sendiri dari frontend

        if (paymentDate.isBefore(salesDate) || paymentDate.isBefore(LocalDate.now()) || paymentDate.isBefore(invoiceDate)) {
            throw new IllegalArgumentException("Tanggal pembayaran tidak boleh sebelum tanggal penjualan dan tanggal tagihan");
        }

        SalesPayment payment = salesOrder.getPayment();

        if (payment == null) {
            payment = new SalesPayment();
            payment.setId(generatePaymentId(salesOrder.getCustomer().getName()));
            payment.setSalesOrder(salesOrder);
            payment.setTotalAmountPayed(BigDecimal.ZERO);
        }

        payment.setPaymentDate(paymentDate);
        payment.setPaymentMethod(request.getPaymentMethod()); // kalo udah partially paid, ini prefilled dari yang udah ada (frontend), 
                                                                // jadi payment methodnya sama terus
 
        BigDecimal additionalPaid = request.getTotalAmountPayed();
        BigDecimal totalPrice = salesOrder.getTotalPrice();
        BigDecimal currentTotalPaid = payment.getTotalAmountPayed();

        BigDecimal newTotalPaid = currentTotalPaid.add(additionalPaid);

        if (newTotalPaid.compareTo(totalPrice) > 0) {
            throw new IllegalArgumentException("Jumlah pembayaran melebihi total harga");
        } else if (newTotalPaid.compareTo(totalPrice) == 0) {
            payment.setPaymentStatus("PAID");
            salesOrder.setStatus("PAID");
            salesOrder.getInvoice().setInvoiceStatus("PAID");
        } else {
            payment.setPaymentStatus("PARTIALLY PAID");
            salesOrder.setStatus("CONFIRMED"); // tetap CONFIRMED hingga lunas
            salesOrder.getInvoice().setInvoiceStatus("PARTIALLY PAID");
        }

        payment.setTotalAmountPayed(newTotalPaid);
        salesOrder.setPayment(payment);
       

        return salesOrderToSalesOrderResponseDTO(salesOrderDb.save(salesOrder));
    }

    // STAGE 4: IN DELIVERY, SURAT JALAN (JESS)
    @Override
    public SalesOrderResponseDTO shipSalesOrder(String id, ShippingRequestDTO request) {
        // Validasi untuk sales order ada atau tidak
        SalesOrder salesOrder = salesOrderDb.findById(id)
            .orElseThrow(() -> new RuntimeException("Sales Order tidak ditemukan"));

        // Validasi tahapan sales order harus "PAID" dan invoice harus "PAID"
        if (!salesOrder.getStatus().equalsIgnoreCase("PAID")) {
            throw new IllegalArgumentException("Sales Order harus dalam status PAID untuk melakukan pembayaran");
        }

        LocalDate salesDate = salesOrder.getSalesDate();
        LocalDate invoiceDate = salesOrder.getInvoice().getInvoiceDate();

        // Validasi untuk tanggal pengiriman tidak boleh sebelum tanggal penjualan atau tidak boleh di masa lalu
        LocalDate shippingDate = request.getShippingDate();
        if (shippingDate.isBefore(salesDate) || shippingDate.isBefore(LocalDate.now()) || shippingDate.isBefore(invoiceDate)) {
            throw new IllegalArgumentException("Tanggal pengiriman tidak boleh sebelum tanggal penjualan dan tanggal tagihan");
        }

        Shipping shipping = new Shipping();
        shipping.setId(generateShippingId(salesOrder.getItems()));
        shipping.setShippingDate(shippingDate);
        shipping.setShippingStatus("IN SHIPPING"); // IN SHIPPING, SHIPPED (KETIKA UDAH SAMPAI NANTI TAHAP SELANJUTNYA)
        shipping.setTrackingNumber(generateTrackingNumber(salesOrder.getItems()));
        shipping.setShippingFee(request.getShippingFee());
        salesOrder.setStatus("IN SHIPPING");
        shipping.setSalesOrder(salesOrder);
        salesOrder.setShipping(shipping);

        // TODO: tambahin attribute di shipping sesuai dengan kebutuhan, utk jess, ubah juga di dto request (ShippingRequestDTO) 
        // dan response (ShippingResponseDTO) sama model (Shipping)

        return salesOrderToSalesOrderResponseDTO(salesOrderDb.save(salesOrder));
    }

    // STAGE 5: COMPLETED, NOTA (MICHAEL)
    @Override
    public SalesOrderResponseDTO completeSalesOrder(String id) {
        // Validasi untuk Sales order ada atau tidak
        SalesOrder salesOrder = salesOrderDb.findById(id)
            .orElseThrow(() -> new RuntimeException("Sales Order tidak ditemukan"));

        // Validasi tahapan sales order harus "IN SHIPPING" dan Shipping status harus "IN SHIPPING"
        if (!salesOrder.getStatus().equalsIgnoreCase("IN SHIPPING")) {
            throw new IllegalArgumentException("Sales Order harus dalam status IN SHIPPING untuk melakukan pembayaran");
        }

        SalesReceipt receipt = new SalesReceipt();
        receipt.setId(generateReceiptId(salesOrder.getItems()));
        receipt.setReceiptDate(salesOrder.getPayment().getPaymentDate()); // tanggal nota = tanggal pembayaran
        receipt.setAmountReceived(salesOrder.getPayment().getTotalAmountPayed());
        salesOrder.setStatus("COMPLETED");
        receipt.setSalesOrder(salesOrder);
        salesOrder.setReceipt(receipt);
        salesOrder.getShipping().setShippingStatus("SHIPPED"); // update status shipping jadi shipped

        // TODO: tambahin attribute di sales receipt sesuai dengan kebutuhan, utk michael, ubah juga di response (PurchaseReceiptResponseDTO) sama model (PurchaseReceipt)

        // Update stock barang di gudang tujuan sesuai dengan quantity yang diterima
        for (SalesOrderItem item : salesOrder.getItems()) {
            Barang barang = item.getBarang();
            for(StockBarangPerGudang stock : barang.getListStockBarang()){
                if(stock.getGudang().getNama().equals(item.getGudangTujuan().getNama())){
                    stock.setStock(stock.getStock() + item.getQuantity()); // update stock barang di gudang tujuan
                }
            }
        }

        return salesOrderToSalesOrderResponseDTO(salesOrderDb.save(salesOrder));
    }

    


    
}