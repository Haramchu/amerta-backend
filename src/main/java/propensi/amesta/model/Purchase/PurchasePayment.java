package propensi.amesta.model.Purchase;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "purchase_payment")
public class PurchasePayment {

    @Id
    private UUID Id;

    @OneToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    private LocalDate paymentDate;
    private String paymentMethod;
    private String paymentStatus;

}
