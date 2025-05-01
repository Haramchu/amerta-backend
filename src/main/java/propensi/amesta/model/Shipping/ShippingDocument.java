package propensi.amesta.model.Shipping;

import java.time.LocalDate;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import propensi.amesta.model.Customer;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shipping_document")
public class ShippingDocument {

    @Id
    private String id;

    @NotNull(message = "Tanggal dokumen tidak boleh kosong")
    private LocalDate documentDate;

    private String purchaseOrderId;
    
    private String salesOrderId;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @NotNull(message = "Alamat pengiriman tidak boleh kosong")
    private String shippingAddress;

    @NotNull(message = "Nama penerima tidak boleh kosong")
    private String recipientName;

    @NotNull(message = "Status dokumen tidak boleh kosong")
    private String status; // DRAFT, SUBMITTED, COMPLETED

    private String notes;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", updatable = false, nullable = false)
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date", nullable = false)
    private Date updatedDate;
}