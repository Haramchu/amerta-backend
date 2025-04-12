package propensi.amesta.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import propensi.amesta.model.Sales.SalesOrder;

@Setter
@Getter
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    private UUID id;

    @OneToMany(mappedBy = "customer")
    private List<SalesOrder> salesOrders;

}
