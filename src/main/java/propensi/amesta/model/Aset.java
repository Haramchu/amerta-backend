package propensi.amesta.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "aset")
public class Aset {

    @Id
    private String nama;

}
