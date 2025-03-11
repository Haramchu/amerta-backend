package propensi.amesta.model.Purchase;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "purchase_receipt")
public class PurchaseReceipt {

    @Id
    private String Id;

}
