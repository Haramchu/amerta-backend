package propensi.amesta.repository.Sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Sales.Shipping;

@Repository
public interface ShippingDb extends JpaRepository<Shipping, String> {

}
