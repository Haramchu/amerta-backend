package propensi.amesta.model.Sales;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sales_receipt")
public class SalesReceipt {

    @Id
    private String Id;

}
