package propensi.amesta.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import propensi.amesta.payload.request.Sales.SalesOrderRequestDTO;
import propensi.amesta.payload.response.BaseResponseDTO;
import propensi.amesta.payload.response.Sales.SalesOrderDetailDTO;
import propensi.amesta.payload.response.Sales.SalesOrderResponseDTO;
import propensi.amesta.service.Sales.SalesOrderService;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales-order")
@RequiredArgsConstructor
public class SalesController {

    @Autowired
    private SalesOrderService salesOrderService;

    @PostMapping("/add")
    public ResponseEntity<BaseResponseDTO<SalesOrderResponseDTO>> createSalesOrder(@Valid @RequestBody SalesOrderRequestDTO requestDTO) {
        BaseResponseDTO<SalesOrderResponseDTO> response = new BaseResponseDTO<SalesOrderResponseDTO>();

        try {
            SalesOrderResponseDTO result = salesOrderService.addSalesOrder(requestDTO);

            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("Sales order berhasil dibuat!");
            response.setTimestamp(new Date());
            response.setData(result);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            BaseResponseDTO<SalesOrderResponseDTO> errorResponse = new BaseResponseDTO<SalesOrderResponseDTO>();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setMessage(e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            BaseResponseDTO<SalesOrderResponseDTO> errorResponse = new BaseResponseDTO<SalesOrderResponseDTO>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Terjadi kesalahan saat membuat sales order");
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/")
    public ResponseEntity<BaseResponseDTO<List<SalesOrderResponseDTO>>> getAllSalesOrders() {
        try {
            List<SalesOrderResponseDTO> salesOrders = salesOrderService.getAllSalesOrders();
            
            BaseResponseDTO<List<SalesOrderResponseDTO>> response = new BaseResponseDTO<List<SalesOrderResponseDTO>>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Daftar sales order berhasil diambil");
            response.setTimestamp(new Date());
            response.setData(salesOrders);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BaseResponseDTO<List<SalesOrderResponseDTO>> errorResponse = new BaseResponseDTO<List<SalesOrderResponseDTO>>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Terjadi kesalahan saat mengambil daftar sales order: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<BaseResponseDTO<List<SalesOrderResponseDTO>>> getSalesOrdersWithFilters(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID customerId) {
        
        try {
            List<SalesOrderResponseDTO> salesOrders = salesOrderService.getSalesOrdersWithFilters(
                    startDate, endDate, status, customerId);
            
            BaseResponseDTO<List<SalesOrderResponseDTO>> response = new BaseResponseDTO<List<SalesOrderResponseDTO>>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Daftar sales order berhasil difilter");
            response.setTimestamp(new Date());
            response.setData(salesOrders);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            BaseResponseDTO<List<SalesOrderResponseDTO>> errorResponse = new BaseResponseDTO<List<SalesOrderResponseDTO>>();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setMessage(e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            BaseResponseDTO<List<SalesOrderResponseDTO>> errorResponse = new BaseResponseDTO<List<SalesOrderResponseDTO>>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Terjadi kesalahan saat memfilter daftar sales order: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<SalesOrderDetailDTO>> getSalesOrderDetail(@PathVariable String id) {
        try {
            SalesOrderDetailDTO salesOrderDetail = salesOrderService.getSalesOrderDetail(id);
            
            BaseResponseDTO<SalesOrderDetailDTO> response = new BaseResponseDTO<SalesOrderDetailDTO>();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Detail sales order berhasil diambil");
            response.setTimestamp(new Date());
            response.setData(salesOrderDetail);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            BaseResponseDTO<SalesOrderDetailDTO> errorResponse = new BaseResponseDTO<SalesOrderDetailDTO>();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setMessage(e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            BaseResponseDTO<SalesOrderDetailDTO> errorResponse = new BaseResponseDTO<SalesOrderDetailDTO>();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setMessage("Terjadi kesalahan saat mengambil detail sales order: " + e.getMessage());
            errorResponse.setTimestamp(new Date());
            errorResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}