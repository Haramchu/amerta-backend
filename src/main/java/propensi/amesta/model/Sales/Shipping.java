package propensi.amesta.model.Sales;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "shipping")
public class Shipping {

    @Id
    private String Id;

}
