package propensi.amesta.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.Customer;
import propensi.amesta.model.Sales.*;
import propensi.amesta.payload.request.SalesOrderItemRequestDTO;
import propensi.amesta.payload.request.SalesOrderRequestDTO;
import propensi.amesta.payload.response.SalesOrderDetailDTO;
import propensi.amesta.payload.response.SalesOrderItemDTO;
import propensi.amesta.payload.response.SalesOrderListDTO;
import propensi.amesta.payload.response.SalesOrderResponseDTO;
import propensi.amesta.repository.Aset.BarangDb;
import propensi.amesta.repository.CustomerDb;
import propensi.amesta.repository.Sales.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl implements SalesOrderService {

    @Autowired
    private SalesOrderDb salesOrderDb;
    @Autowired
    private CustomerDb customerDb;
    @Autowired
    private BarangDb barangDb;
    @Autowired
    private SalesInvoiceDb salesInvoiceDb;
    @Autowired
    private SalesReceiptDb salesReceiptDb;
    @Autowired
    private ShippingDb shippingDb;

    @Override
    @Transactional
    public SalesOrderResponseDTO addSalesOrder(SalesOrderRequestDTO request) {
        Customer customer = customerDb.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer tidak ditemukan"));

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(UUID.randomUUID().toString());
        salesOrder.setCustomerId(customer.getId());
        salesOrder.setOrderDate(request.getOrderDate());
        salesOrder.setStatus("CREATED");

        BigDecimal total = BigDecimal.ZERO;
        var items = new ArrayList<SalesOrderItem>();

        for (SalesOrderItemRequestDTO itemDTO : request.getItems()) {
            Barang barang = barangDb.findById(itemDTO.getBarangId())
                    .orElseThrow(() -> new IllegalArgumentException("Barang dengan ID " + itemDTO.getBarangId() + " tidak ditemukan"));

            BigDecimal unitPrice = barang.getHarga();
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            total = total.add(itemTotal);

            SalesOrderItem item = new SalesOrderItem();
            item.setId(UUID.randomUUID().toString());
            item.setSalesOrder(salesOrder);
            item.setBarang(barang);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(unitPrice);

            items.add(item);
        }

        salesOrder.setItems(items);
        salesOrder.setTotalPrice(total);

        salesOrderDb.save(salesOrder);

        SalesOrderResponseDTO response = new SalesOrderResponseDTO();
        response.setId(salesOrder.getId());
        response.setCustomerId(customer.getId());
        response.setOrderDate(salesOrder.getOrderDate());
        response.setStatus(salesOrder.getStatus());
        response.setTotalPrice(total);

        return response;
    }

    @Override
    public List<SalesOrderListDTO> getAllSalesOrders() {
        List<SalesOrder> salesOrders = salesOrderDb.findAll();
        return convertToSalesOrderListDTOs(salesOrders);
    }

    @Override
    public List<SalesOrderListDTO> getSalesOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        List<SalesOrder> salesOrders = salesOrderDb.findByOrderDateBetween(startDate, endDate);
        return convertToSalesOrderListDTOs(salesOrders);
    }

    @Override
    public List<SalesOrderListDTO> getSalesOrdersByStatus(String status) {
        List<SalesOrder> salesOrders = salesOrderDb.findByStatus(status);
        return convertToSalesOrderListDTOs(salesOrders);
    }

    @Override
    public List<SalesOrderListDTO> getSalesOrdersByCustomer(UUID customerId) {
        Customer customer = customerDb.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer tidak ditemukan"));
        List<SalesOrder> salesOrders = salesOrderDb.findByCustomerId(customer.getId());
        return convertToSalesOrderListDTOs(salesOrders);
    }

    @Override
    public List<SalesOrderListDTO> getSalesOrdersWithFilters(
            LocalDate startDate, LocalDate endDate, String status, UUID customerId) {

        if (startDate == null && endDate == null && status == null && customerId == null) {
            return getAllSalesOrders();
        }

        if (startDate == null) {
            startDate = LocalDate.of(2000, 1, 1);
        }
        if (endDate == null) {
            endDate = LocalDate.now().plusYears(10);
        }

        List<SalesOrder> salesOrders;
        if (status != null && customerId != null) {
            salesOrders = salesOrderDb.findByOrderDateBetweenAndStatusAndCustomerId(startDate, endDate, status, customerId);
        } else if (status != null) {
            salesOrders = salesOrderDb.findByOrderDateBetweenAndStatus(startDate, endDate, status);
        } else if (customerId != null) {
            salesOrders = salesOrderDb.findByOrderDateBetweenAndCustomerId(startDate, endDate, customerId);
        } else {
            salesOrders = salesOrderDb.findByOrderDateBetween(startDate, endDate);
        }
        
        return convertToSalesOrderListDTOs(salesOrders);
    }

    @Override
    public SalesOrderDetailDTO getSalesOrderDetail(String salesOrderId) {
        SalesOrder salesOrder = salesOrderDb.findById(salesOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Sales order tidak ditemukan"));
        
        Customer customer = customerDb.findById(salesOrder.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer tidak ditemukan"));
        
        SalesOrderDetailDTO detailDTO = new SalesOrderDetailDTO();
        detailDTO.setId(salesOrder.getId());
        detailDTO.setOrderDate(salesOrder.getOrderDate());
        detailDTO.setStatus(salesOrder.getStatus());
        detailDTO.setCustomerId(customer.getId().toString());
        detailDTO.setCustomerName(customer.getName());
        detailDTO.setTotalPrice(salesOrder.getTotalPrice());
        
        List<SalesOrderItemDTO> itemDTOs = new ArrayList<>();
        for (SalesOrderItem item : salesOrder.getItems()) {
            SalesOrderItemDTO itemDTO = new SalesOrderItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setBarangId(item.getBarang().getId());
            itemDTO.setBarangName(item.getBarang().getNama());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setUnitPrice(item.getUnitPrice());
            itemDTO.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            itemDTOs.add(itemDTO);
        }
        detailDTO.setItems(itemDTOs);

        if (salesOrder.getInvoice() != null) {
            detailDTO.setInvoiceId(salesOrder.getInvoice().getId());
            detailDTO.setInvoiceDate(salesOrder.getInvoice().getInvoiceDate());
            detailDTO.setInvoiceAmount(salesOrder.getInvoice().getAmount());
        }
        
        if (salesOrder.getShipping() != null) {
            detailDTO.setShippingId(salesOrder.getShipping().getId());
            detailDTO.setShippingDate(salesOrder.getShipping().getShippingDate());
            detailDTO.setShippingStatus(salesOrder.getShipping().getShippingStatus());
            detailDTO.setTrackingNumber(salesOrder.getShipping().getTrackingNumber());
        }
        
        if (salesOrder.getReceipt() != null) {
            detailDTO.setReceiptId(salesOrder.getReceipt().getId());
            detailDTO.setReceiptDate(salesOrder.getReceipt().getReceiptDate());
            detailDTO.setAmountReceived(salesOrder.getReceipt().getAmountReceived());
        }
        
        return detailDTO;
    }

    private List<SalesOrderListDTO> convertToSalesOrderListDTOs(List<SalesOrder> salesOrders) {
        return salesOrders.stream().map(order -> {
            Customer customer = customerDb.findById(order.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer tidak ditemukan"));
                    
            SalesOrderListDTO dto = new SalesOrderListDTO();
            dto.setId(order.getId());
            dto.setOrderDate(order.getOrderDate());
            dto.setStatus(order.getStatus());
            dto.setCustomerName(customer.getName());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setHasInvoice(order.getInvoice() != null);
            dto.setHasShipping(order.getShipping() != null);
            dto.setHasReceipt(order.getReceipt() != null);
            return dto;
        }).collect(Collectors.toList());
    }
}