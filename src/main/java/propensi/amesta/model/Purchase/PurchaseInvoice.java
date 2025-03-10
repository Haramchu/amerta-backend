package propensi.amesta.model.Purchase;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "purchase_invoice")
public class PurchaseInvoice {

    @Id
    private String Id;

}
