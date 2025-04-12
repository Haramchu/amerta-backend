package propensi.amesta.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Customer;

@Repository
public interface CustomerDb extends JpaRepository<Customer, UUID> {
    
    Customer findByEmail(String email);

    Customer findByUsername(String username);

}
