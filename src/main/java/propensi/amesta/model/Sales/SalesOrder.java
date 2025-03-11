package propensi.amesta.model.Sales;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sales_order")
public class SalesOrder {

    @Id
    private String Id;

}
