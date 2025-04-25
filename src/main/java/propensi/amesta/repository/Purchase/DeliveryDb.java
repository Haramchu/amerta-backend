package propensi.amesta.repository.Purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Purchase.Delivery;

@Repository
public interface DeliveryDb extends JpaRepository<Delivery, String> {
    
}
