package propensi.amesta.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import propensi.amesta.model.Purchase.PurchaseOrder;
import propensi.amesta.model.Sales.SalesOrder;

@Setter
@Getter
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    private UUID id;

    private String name;
    private String phone;
    private String handphone;
    private String whatsapp;
    private String email;
    private String address;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<SalesOrder> salesOrders;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<PurchaseOrder> purchaseOrders;

    private String role;

}
