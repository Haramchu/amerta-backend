package propensi.amesta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.amesta.model.Customer;
import propensi.amesta.payload.request.CustomerRequestDTO;
import propensi.amesta.payload.response.CustomerResponseDTO;
import propensi.amesta.repository.CustomerDb;

import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerDb customerDb;

    public CustomerResponseDTO addCustomer(CustomerRequestDTO request) {
        Customer existing = customerDb.findByEmail(request.getEmail());
        if (existing != null) {
            throw new IllegalArgumentException("Email sudah terdaftar.");
        }

        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setHandphone(request.getHandphone());
        customer.setWhatsapp(request.getWhatsapp());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setRole(request.getRole());

        Customer saved = customerDb.save(customer);

        return customerToCustomerResponseDTO(saved);
    }

    private CustomerResponseDTO customerToCustomerResponseDTO(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getHandphone(),
                customer.getWhatsapp(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getRole()
        );
    }
}
