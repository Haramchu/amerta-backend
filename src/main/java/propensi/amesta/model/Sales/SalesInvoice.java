package propensi.amesta.model.Sales;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sales_invoice")
public class SalesInvoice {

    @Id
    private String Id;

}
