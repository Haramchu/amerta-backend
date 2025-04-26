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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import propensi.amesta.payload.request.Purchase.PurchaseOrderRequestDTO;
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

    @GetMapping("/list")
    public ResponseEntity<?> getAllPurchaseOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID supplierId) {
        
        try {
            List<PurchaseOrderResponseDTO> purchaseOrders;
            
            // Jika semua parameter kosong, ambil semua purchase orders
            if (startDate == null && endDate == null && status == null && supplierId == null) {
                purchaseOrders = purchaseOrderService.getAllPurchaseOrders();
            } else {
                // Jika ada parameter filter, gunakan metode filter
                purchaseOrders = purchaseOrderService.getPurchaseOrdersWithFilters(startDate, endDate, status, supplierId);
            }
            
            BaseResponseDTO<List<PurchaseOrderResponseDTO>> response = new BaseResponseDTO<>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Daftar purchase order berhasil diambil");
            response.setTimestamp(new Date());
            response.setData(purchaseOrders);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            BaseResponseDTO<List<PurchaseOrderResponseDTO>> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setMessage(e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            BaseResponseDTO<List<PurchaseOrderResponseDTO>> errorResponse = new BaseResponseDTO<>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Terjadi kesalahan saat mengambil daftar purchase order: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPurchaseOrderDetail(@PathVariable String id) {
        try {
            PurchaseOrderResponseDTO purchaseOrderDetail = purchaseOrderService.getPurchaseOrderDetail(id);
            
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
}