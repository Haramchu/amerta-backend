package propensi.amesta.controller.Purchase;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import propensi.amesta.payload.request.Purchase.DeliveryRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchaseOrderInvoiceRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchaseOrderRequestDTO;
import propensi.amesta.payload.request.Purchase.PurchasePaymentRequestDTO;
import propensi.amesta.payload.response.BaseResponseDTO;
import propensi.amesta.payload.response.Purchase.PurchaseOrderResponseDTO;
import propensi.amesta.service.Purchase.PurchaseOrderService;

@RestController
@RequestMapping("/api/purchase-order")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping("/add")
    public ResponseEntity<?> addPurchaseOrder(@Valid @RequestBody PurchaseOrderRequestDTO purchaseOrderRequestDTO, BindingResult bindingResult) {
        BaseResponseDTO<PurchaseOrderResponseDTO> baseResponseDTO = new BaseResponseDTO<>();
        
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
            PurchaseOrderResponseDTO purchaseOrderResponseDTO = purchaseOrderService.addPurchaseOrder(purchaseOrderRequestDTO);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Purchase Order berhasil ditambahkan.");
            baseResponseDTO.setData(purchaseOrderResponseDTO);
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
    public ResponseEntity<?> getAllPurchaseOrder(@RequestParam(required = false) String status) {
        BaseResponseDTO<List<PurchaseOrderResponseDTO>> baseResponseDTO = new BaseResponseDTO<>();
        try {
            List<PurchaseOrderResponseDTO> listBarang;
            
            if (status != null && !status.isEmpty()) {
                listBarang = purchaseOrderService.getPurchaseOrdersByStatus(status);
                baseResponseDTO.setMessage("Purchase Orders dengan status " + status + " berhasil ditemukan.");
            } else {
                listBarang = purchaseOrderService.getAllPurchaseOrders();
                baseResponseDTO.setMessage("Purchase Orders berhasil ditemukan.");
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
    public ResponseEntity<?> getPurchaseOrderById(@PathVariable String id) {
        try {
            PurchaseOrderResponseDTO purchaseOrderDetail = purchaseOrderService.getPurchaseOrderById(id);
            
            BaseResponseDTO<PurchaseOrderResponseDTO> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Detail purchase order berhasil diambil");
            response.setTimestamp(new Date());
            response.setData(purchaseOrderDetail);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            BaseResponseDTO<PurchaseOrderResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setMessage(e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            BaseResponseDTO<PurchaseOrderResponseDTO> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Terjadi kesalahan saat mengambil detail purchase order: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/confirm/{id}")
    public ResponseEntity<?> confirmPurchaseOrder(@PathVariable String id, 
    @Valid @RequestBody PurchaseOrderInvoiceRequestDTO PoInvoiceRequestDTO, BindingResult bindingResult) {
        BaseResponseDTO<PurchaseOrderResponseDTO> baseResponseDTO = new BaseResponseDTO<>();
        
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
            PurchaseOrderResponseDTO purchaseOrderResponseDTO = purchaseOrderService.confirmPurchaseOrder(id, PoInvoiceRequestDTO);
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
    public ResponseEntity<?> payPurchaseOrder(@PathVariable String id, 
    @Valid @RequestBody PurchasePaymentRequestDTO PoPaymentRequestDTO, BindingResult bindingResult) {
        BaseResponseDTO<PurchaseOrderResponseDTO> baseResponseDTO = new BaseResponseDTO<>();
        
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
            PurchaseOrderResponseDTO purchaseOrderResponseDTO = purchaseOrderService.payPurchaseOrder(id, PoPaymentRequestDTO);
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

    @PutMapping("/delivery/{id}")
    public ResponseEntity<?> deliverPurchaseOrder(@PathVariable String id, 
    @Valid @RequestBody DeliveryRequestDTO deliveryPoRequestDTO, BindingResult bindingResult) {
        BaseResponseDTO<PurchaseOrderResponseDTO> baseResponseDTO = new BaseResponseDTO<>();
        
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
            PurchaseOrderResponseDTO purchaseOrderResponseDTO = purchaseOrderService.deliverPurchaseOrder(id, deliveryPoRequestDTO);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Pesanan di Purchase Order sedang dikirim.");
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

    @PutMapping("/complete/{id}")
    public ResponseEntity<?> deliverPurchaseOrder(@PathVariable String id) {
        BaseResponseDTO<PurchaseOrderResponseDTO> baseResponseDTO = new BaseResponseDTO<>();
        
        try {
            PurchaseOrderResponseDTO purchaseOrderResponseDTO = purchaseOrderService.completePurchaseOrder(id);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Purchase Order berhasil diselesaikan.");
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
}