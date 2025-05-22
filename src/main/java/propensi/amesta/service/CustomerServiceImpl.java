package propensi.amesta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.amesta.model.Customer;
import propensi.amesta.payload.request.CustomerRequestDTO;
import propensi.amesta.payload.request.CustomerUpdateRequestDTO;
import propensi.amesta.payload.response.CustomerResponseDTO;
import propensi.amesta.repository.CustomerDb;

import java.util.List;
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

    public List<CustomerResponseDTO> getAllCustomer() {
        List<Customer> customers = customerDb.findAll();
        
        return customers.stream()
                .map(this::customerToCustomerResponseDTO)
                .toList();
    }

    public CustomerResponseDTO getCustomerById(UUID idCustomer) {
        Customer customer = customerDb.findById(idCustomer).orElseThrow(() -> new IllegalArgumentException("Customer tidak ditemukan."));
        return customerToCustomerResponseDTO(customer);
    }

    public CustomerResponseDTO updateCustomer(UUID idCustomer, CustomerUpdateRequestDTO request) {
        Customer customer = customerDb.findById(idCustomer)
                .orElseThrow(() -> new IllegalArgumentException("Customer tidak ditemukan."));

        // Check if email is being changed and if it's already taken
        if (!customer.getEmail().equals(request.getEmail())) {
            Customer existingWithEmail = customerDb.findByEmail(request.getEmail());
            if (existingWithEmail != null) {
                throw new IllegalArgumentException("Email sudah terdaftar.");
            }
        }

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setHandphone(request.getHandphone());
        customer.setWhatsapp(request.getWhatsapp());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());

        Customer updated = customerDb.save(customer);
        return customerToCustomerResponseDTO(updated);
    }
}