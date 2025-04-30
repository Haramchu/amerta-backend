package propensi.amesta.model.Sales;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sales_receipt")
public class SalesReceipt {

    @Id
    private String Id;

    @OneToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

    private LocalDate receiptDate;
    private BigDecimal amountReceived;

}
