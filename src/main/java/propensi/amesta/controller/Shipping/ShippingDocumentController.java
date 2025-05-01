package propensi.amesta.controller.Shipping;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.DocumentException;

import jakarta.validation.Valid;
import propensi.amesta.payload.request.Shipping.ShippingDocumentRequestDTO;
import propensi.amesta.payload.response.BaseResponseDTO;
import propensi.amesta.payload.response.Shipping.ShippingDocumentResponseDTO;
import propensi.amesta.service.Shipping.PDFExportService;
import propensi.amesta.service.Shipping.ShippingDocumentService;

@RestController
@RequestMapping("/api/shipping")
public class ShippingDocumentController {

    @Autowired
    private PDFExportService pdfExportService;

    @Autowired
    private ShippingDocumentService shippingDocumentService;
    
    @PostMapping("/create")
    public ResponseEntity<?> createShippingDocument(@Valid @RequestBody ShippingDocumentRequestDTO request, BindingResult bindingResult) {
        BaseResponseDTO<ShippingDocumentResponseDTO> response = new BaseResponseDTO<>();
        
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(errorMessages.toString());
            response.setTimestamp(new Date());
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            ShippingDocumentResponseDTO result = shippingDocumentService.createShippingDocument(request);
            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("Surat jalan berhasil dibuat");
            response.setData(result);
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan: " + e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/generate/purchase-order/{id}")
    public ResponseEntity<?> generateFromPurchaseOrder(@PathVariable("id") String purchaseOrderId) {
        BaseResponseDTO<ShippingDocumentResponseDTO> response = new BaseResponseDTO<>();
        
        try {
            ShippingDocumentResponseDTO result = shippingDocumentService.generateFromPurchaseOrder(purchaseOrderId);
            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("Surat jalan berhasil dibuat dari Purchase Order");
            response.setData(result);
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan: " + e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/generate/sales-order/{id}")
    public ResponseEntity<?> generateFromSalesOrder(@PathVariable("id") String salesOrderId) {
        BaseResponseDTO<ShippingDocumentResponseDTO> response = new BaseResponseDTO<>();
        
        try {
            ShippingDocumentResponseDTO result = shippingDocumentService.generateFromSalesOrder(salesOrderId);
            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("Surat jalan berhasil dibuat dari Sales Order");
            response.setData(result);
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan: " + e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/")
    public ResponseEntity<?> getAllShippingDocuments() {
        BaseResponseDTO<List<ShippingDocumentResponseDTO>> response = new BaseResponseDTO<>();
        
        try {
            List<ShippingDocumentResponseDTO> results = shippingDocumentService.getAllShippingDocuments();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Daftar surat jalan berhasil diambil");
            response.setData(results);
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan: " + e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getShippingDocumentById(@PathVariable("id") String id) {
        BaseResponseDTO<ShippingDocumentResponseDTO> response = new BaseResponseDTO<>();
        
        try {
            ShippingDocumentResponseDTO result = shippingDocumentService.getShippingDocumentById(id);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Surat jalan berhasil ditemukan");
            response.setData(result);
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan: " + e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchShippingDocuments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UUID customerId) {
        
        BaseResponseDTO<List<ShippingDocumentResponseDTO>> response = new BaseResponseDTO<>();
        
        try {
            List<ShippingDocumentResponseDTO> results = shippingDocumentService.getShippingDocumentsWithFilters(
                    status, startDate, endDate, customerId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Daftar surat jalan berhasil ditemukan");
            response.setData(results);
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan: " + e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable("id") String id, @RequestParam String status) {
        BaseResponseDTO<ShippingDocumentResponseDTO> response = new BaseResponseDTO<>();
        
        try {
            ShippingDocumentResponseDTO result = shippingDocumentService.updateStatus(id, status);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Status surat jalan berhasil diupdate");
            response.setData(result);
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan: " + e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateShippingDocument(
            @PathVariable("id") String id,
            @Valid @RequestBody ShippingDocumentRequestDTO request,
            BindingResult bindingResult) {
        
        BaseResponseDTO<ShippingDocumentResponseDTO> response = new BaseResponseDTO<>();
        
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(errorMessages.toString());
            response.setTimestamp(new Date());
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            ShippingDocumentResponseDTO result = shippingDocumentService.updateShippingDocument(id, request);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Surat jalan berhasil diupdate");
            response.setData(result);
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan: " + e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteShippingDocument(@PathVariable("id") String id) {
        BaseResponseDTO<Void> response = new BaseResponseDTO<>();
        
        try {
            shippingDocumentService.deleteShippingDocument(id);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Surat jalan berhasil dihapus");
            response.setTimestamp(new Date());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Terjadi kesalahan: " + e.getMessage());
            response.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/export-pdf/{id}")
    public ResponseEntity<?> exportShippingDocumentPDF(@PathVariable("id") String id) {
        try {
            // First, check if the shipping document exists
            shippingDocumentService.getShippingDocumentById(id);
            
            // Generate PDF
            byte[] pdfContent = pdfExportService.generateShippingDocumentPDF(id);
            
            // Set up response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "shipping-document-" + id + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Shipping document not found: " + e.getMessage());
                    
        } catch (DocumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating PDF: " + e.getMessage());
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }
}