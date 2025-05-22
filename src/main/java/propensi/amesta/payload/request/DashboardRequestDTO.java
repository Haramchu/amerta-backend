package propensi.amesta.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardRequestDTO {
    private String entity;         // e.g., "purchase_invoice", "sales_order"
    private String x;              // e.g., "invoiceDate", "status", "barang.name"
    private String y;              // e.g., "totalAmount", "quantity"
    private String aggregation;    // e.g., "sum", "count", "avg", "max", "min"

    private FilterDashboardDTO filter;      // nested DTO untuk filtering (optional)
}
