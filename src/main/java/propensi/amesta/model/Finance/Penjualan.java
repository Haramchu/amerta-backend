package propensi.amesta.model.Finance;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "penjualan")
public class Penjualan {

    @Id
    private UUID id;

}
