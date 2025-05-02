package propensi.amesta.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    @GetMapping("/viewall")
    public ResponseEntity<?> getAllCustomers() {

        BaseResponseDTO<List<CustomerResponseDTO>> baseResponseDTO = new BaseResponseDTO<>();
        try {
            List<CustomerResponseDTO> listCustomer = customerService.getAllCustomer();
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Customer berhasil ditemukan.");
            baseResponseDTO.setData(listCustomer);
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

    @GetMapping("/{idCustomer}")
    public ResponseEntity<?> getCustomerById(@PathVariable("idCustomer") UUID idCustomer) {
        BaseResponseDTO<CustomerResponseDTO> baseResponseDTO = new BaseResponseDTO<>();
        try {
            CustomerResponseDTO customer = customerService.getCustomerById(idCustomer);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setMessage("Customer berhasil ditemukan.");
            baseResponseDTO.setData(customer);
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

}
