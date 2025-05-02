package propensi.amesta.enums.Purchase;

public enum InvoiceStatus {
    DRAFT("DRAFT"),
    ISSUED("ISSUED"),
    PAID("PAID");

    private final String status;

    InvoiceStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static InvoiceStatus fromString(String status) {
        for (InvoiceStatus invoiceStatus : InvoiceStatus.values()) {
            if (invoiceStatus.status.equalsIgnoreCase(status)) {
                return invoiceStatus;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return status;
    }
}