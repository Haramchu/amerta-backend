package propensi.amesta.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import propensi.amesta.payload.request.Sales.SalesOrderRequestDTO;
import propensi.amesta.payload.response.BaseResponseDTO;
import propensi.amesta.payload.response.Sales.SalesOrderResponseDTO;
import propensi.amesta.service.SalesOrderService;

import java.util.Date;

@RestController
@RequestMapping("/api/sales-order")
@RequiredArgsConstructor
public class SalesController {

    @Autowired
    private SalesOrderService salesOrderService;

    @PostMapping("/add")
    public ResponseEntity<?> createSalesOrder(@Valid @RequestBody SalesOrderRequestDTO requestDTO) {
        BaseResponseDTO<SalesOrderResponseDTO> response = new BaseResponseDTO<>();

        try {
            SalesOrderResponseDTO result = salesOrderService.addSalesOrder(requestDTO);

            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("Sales order berhasil dibuat!");
            response.setTimestamp(new Date());
            response.setData(result);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponseDTO<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), new Date(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Terjadi kesalahan saat membuat sales order", new Date(), null));
        }
    }
}
