package propensi.amesta.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.Customer;
import propensi.amesta.model.Sales.SalesOrder;
import propensi.amesta.model.Sales.SalesOrderItem;
import propensi.amesta.payload.request.Sales.SalesOrderItemRequestDTO;
import propensi.amesta.payload.request.Sales.SalesOrderRequestDTO;
import propensi.amesta.payload.response.Sales.SalesOrderResponseDTO;
import propensi.amesta.repository.Aset.BarangDb;
import propensi.amesta.repository.CustomerDb;
import propensi.amesta.repository.Sales.SalesOrderDb;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl implements SalesOrderService {

    @Autowired
    private SalesOrderDb salesOrderDb;
    @Autowired
    private CustomerDb customerDb;
    @Autowired
    private BarangDb barangDb;

    @Override
    @Transactional
    public SalesOrderResponseDTO addSalesOrder(SalesOrderRequestDTO request) {
        Customer customer = customerDb.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer tidak ditemukan"));

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(UUID.randomUUID().toString());
        salesOrder.setCustomer(customer);
        salesOrder.setOrderDate(request.getOrderDate());
        salesOrder.setStatus("CREATED");

        BigDecimal total = BigDecimal.ZERO;
        var items = new ArrayList<SalesOrderItem>();

        for (SalesOrderItemRequestDTO itemDTO : request.getItems()) {
            Barang barang = barangDb.findById(itemDTO.getBarangId())
                    .orElseThrow(() -> new IllegalArgumentException("Barang dengan ID " + itemDTO.getBarangId() + " tidak ditemukan"));

            BigDecimal unitPrice = barang.getHargaJual();
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            total = total.add(itemTotal);

            SalesOrderItem item = new SalesOrderItem();
            

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
}
