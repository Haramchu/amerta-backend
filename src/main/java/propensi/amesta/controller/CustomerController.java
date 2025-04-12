package propensi.amesta.controller;

import java.util.Date;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import propensi.amesta.payload.request.CustomerRequestDTO;
import propensi.amesta.payload.response.BaseResponseDTO;
import propensi.amesta.payload.response.CustomerResponseDTO;
import propensi.amesta.service.CustomerService;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/add")
    public ResponseEntity<?> addCustomer(@Valid @RequestBody CustomerRequestDTO requestDTO) {
        BaseResponseDTO<CustomerResponseDTO> response = new BaseResponseDTO<>();

        try {
            CustomerResponseDTO result = customerService.addCustomer(requestDTO);

            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage("Customer berhasil ditambahkan!");
            response.setTimestamp(new Date());
            response.setData(result);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponseDTO<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), new Date(), null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Terjadi kesalahan saat menambahkan customer!", new Date(), null));
        }
    }
}
