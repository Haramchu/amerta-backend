package propensi.amesta.model.Aset;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "transfer_barang")
public class TransferBarang {

    @Id
    private String id;

}
