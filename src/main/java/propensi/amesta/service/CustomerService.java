package propensi.amesta.service;

import java.util.List;
import java.util.UUID;

import propensi.amesta.payload.request.CustomerRequestDTO;
import propensi.amesta.payload.response.CustomerResponseDTO;

public interface CustomerService {

    CustomerResponseDTO addCustomer (CustomerRequestDTO customer);

    List<CustomerResponseDTO> getAllCustomer ();

    CustomerResponseDTO getCustomerById (UUID idCustomer);

}
