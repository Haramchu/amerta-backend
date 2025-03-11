package propensi.amesta.model.Aset;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "gudang")
public class Gudang {

    @Id
    private String nama;

}
