package propensi.amesta.controller.Purchase;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
