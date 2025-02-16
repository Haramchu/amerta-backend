package propensi.amesta.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "stok")
public class Stok {

    @Id
    private String serial;

}
