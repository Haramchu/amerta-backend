package propensi.amesta.model.Finance;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "penerimaan")
public class Penerimaan {

    @Id
    private UUID id;

}
