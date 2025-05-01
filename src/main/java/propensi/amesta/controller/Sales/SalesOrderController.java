package propensi.amesta.controller.Sales;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import propensi.amesta.payload.request.Sales.ShippingRequestDTO;
import propensi.amesta.payload.request.Sales.SalesOrderInvoiceRequestDTO;
import propensi.amesta.payload.request.Sales.SalesOrderRequestDTO;
import propensi.amesta.payload.request.Sales.SalesPaymentRequestDTO;
import propensi.amesta.payload.response.BaseResponseDTO;
import propensi.amesta.payload.response.Sales.SalesOrderResponseDTO;
import propensi.amesta.service.Sales.SalesOrderService;

@RestController
@RequestMapping("/api/sales-order")
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;

    @PostMapping("/add")
    public ResponseEntity<?> addSalesOrder(@Valid @RequestBody SalesOrderRequestDTO salesOrderRequestDTO,
            BindingResult bindingResult) {
        BaseResponseDTO<SalesOrderResponseDTO> baseResponseDTO = new BaseResponseDTO<>();

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            SalesOrderResponseDTO salesOrderResponseDTO = salesOrderService.addSalesOrder(salesOrderRequestDTO);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Sales Order berhasil ditambahkan.");
            baseResponseDTO.setData(salesOrderResponseDTO);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Terjadi kesalahan pada server: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/viewall")
    public ResponseEntity<?> getAllSalesOrder(@RequestParam(required = false) String status) {
        BaseResponseDTO<List<SalesOrderResponseDTO>> baseResponseDTO = new BaseResponseDTO<>();
        try {
            List<SalesOrderResponseDTO> listBarang;
            
            if (status != null && !status.isEmpty()) {
                listBarang = salesOrderService.getSalesOrdersByStatus(status);
                baseResponseDTO.setMessage("Sales Orders dengan status " + status + " berhasil ditemukan.");
            } else {
                listBarang = salesOrderService.getAllSalesOrders();
                baseResponseDTO.setMessage("Sales Orders berhasil ditemukan.");
            }
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(listBarang);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Terjadi kesalahan pada server: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSalesOrderById(@PathVariable("id") String id) {
        BaseResponseDTO<SalesOrderResponseDTO> baseResponseDTO = new BaseResponseDTO<>();
        try {
            SalesOrderResponseDTO barangResponseDTO = salesOrderService.getSalesOrderById(id);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Sales Order berhasil ditemukan.");
            baseResponseDTO.setData(barangResponseDTO);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Terjadi kesalahan pada server: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // @GetMapping("/")
    // public ResponseEntity<BaseResponseDTO<List<SalesOrderResponseDTO>>> getSalesOrdersWithFilters(
    //         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    //         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
    //         @RequestParam(required = false) String status,
    //         @RequestParam(required = false) UUID customerId) {
        
    //     try {
    //         List<SalesOrderResponseDTO> salesOrders = salesOrderService.getAllSalesOrders(
    //                 startDate, endDate, status, customerId);
            
    //         BaseResponseDTO<List<SalesOrderResponseDTO>> response = new BaseResponseDTO<>();
    //         response.setStatus(HttpStatus.OK.value());
    //         response.setMessage("Daftar sales order berhasil diambil");
    //         response.setTimestamp(new Date());
    //         response.setData(salesOrders);
            
    //         return ResponseEntity.ok(response);
    //     } catch (IllegalArgumentException e) {
    //         BaseResponseDTO<List<SalesOrderResponseDTO>> errorResponse = new BaseResponseDTO<>();
    //         errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
    //         errorResponse.setMessage(e.getMessage());
    //         errorResponse.setTimestamp(new Date());
    //         errorResponse.setData(null);
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    //     } catch (Exception e) {
    //         BaseResponseDTO<List<SalesOrderResponseDTO>> errorResponse = new BaseResponseDTO<>();
    //         errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    //         errorResponse.setMessage("Terjadi kesalahan saat mengambil daftar sales order: " + e.getMessage());
    //         errorResponse.setTimestamp(new Date());
    //         errorResponse.setData(null);
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    //     }
    // }

    @PutMapping("/confirm/{id}")
    public ResponseEntity<?> confirmSalesOrder(@PathVariable String id,
            @Valid @RequestBody SalesOrderInvoiceRequestDTO PoInvoiceRequestDTO, BindingResult bindingResult) {
        BaseResponseDTO<SalesOrderResponseDTO> baseResponseDTO = new BaseResponseDTO<>();

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            SalesOrderResponseDTO purchaseOrderResponseDTO = salesOrderService.confirmSalesOrder(id,
                    PoInvoiceRequestDTO);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Purchase Order berhasil dikonfirmasi.");
            baseResponseDTO.setData(purchaseOrderResponseDTO);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Terjadi kesalahan pada server: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/payment/{id}")
    public ResponseEntity<?> paySalesOrder(@PathVariable String id,
            @Valid @RequestBody SalesPaymentRequestDTO PoPaymentRequestDTO, BindingResult bindingResult) {
        BaseResponseDTO<SalesOrderResponseDTO> baseResponseDTO = new BaseResponseDTO<>();

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            SalesOrderResponseDTO purchaseOrderResponseDTO = salesOrderService.paySalesOrder(id, PoPaymentRequestDTO);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Sales Order berhasil dikonfirmasi.");
            baseResponseDTO.setData(purchaseOrderResponseDTO);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Terjadi kesalahan pada server: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/shipping/{id}")
    public ResponseEntity<?> shipSalesOrder(@PathVariable String id,
            @Valid @RequestBody ShippingRequestDTO shippingSalesOrderRequestDTO, BindingResult bindingResult) {
        BaseResponseDTO<SalesOrderResponseDTO> baseResponseDTO = new BaseResponseDTO<>();

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            SalesOrderResponseDTO salesOrderResponseDTO = salesOrderService.shipSalesOrder(id,
                    shippingSalesOrderRequestDTO);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Pesanan di Purchase Order sedang dikirim.");
            baseResponseDTO.setData(salesOrderResponseDTO);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Terjadi kesalahan pada server: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/confirm-shipping/{id}")
    public ResponseEntity<?> confirmShipping(@PathVariable String id) {
        BaseResponseDTO<SalesOrderResponseDTO> baseResponseDTO = new BaseResponseDTO<>();

        try {
            SalesOrderResponseDTO salesOrderResponseDTO = salesOrderService.confirmShipping(id);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Pengiriman berhasil dikonfirmasi, barang sudah sampai.");
            baseResponseDTO.setData(salesOrderResponseDTO);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Terjadi kesalahan pada server: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
