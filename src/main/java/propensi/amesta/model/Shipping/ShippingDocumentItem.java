package propensi.amesta.model.Shipping;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.Aset.Gudang;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shipping_document_item")
public class ShippingDocumentItem {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "shipping_document_id", referencedColumnName = "id", nullable = false)
    private ShippingDocument shippingDocument;

    @ManyToOne
    @JoinColumn(name = "barang_id", referencedColumnName = "id", nullable = false)
    private Barang barang;

    @NotNull(message = "Kuantitas tidak boleh kosong")
    @Min(value = 1, message = "Kuantitas minimal 1")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "gudang_id", referencedColumnName = "nama")
    private Gudang gudang;
}