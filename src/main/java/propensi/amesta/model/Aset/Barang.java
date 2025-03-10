package propensi.amesta.model.Aset;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "barang")
public class Barang {

    @Id
    private String nama;

}
