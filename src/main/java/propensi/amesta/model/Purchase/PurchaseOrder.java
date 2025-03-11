package propensi.amesta.model.Purchase;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "purchase_order")
public class PurchaseOrder {

    @Id
    private String Id;

}
