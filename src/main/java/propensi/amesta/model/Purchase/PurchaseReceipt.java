package propensi.amesta.model.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "purchase_receipt")
public class PurchaseReceipt {

    @Id
    private String Id;

    @OneToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    private LocalDate receiptDate;
    private BigDecimal amountPayed;
}