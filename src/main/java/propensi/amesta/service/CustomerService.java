package propensi.amesta.service;

import propensi.amesta.payload.request.CustomerRequestDTO;
import propensi.amesta.payload.response.CustomerResponseDTO;

public interface CustomerService {

    CustomerResponseDTO addCustomer (CustomerRequestDTO customer);

}
