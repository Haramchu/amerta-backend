package propensi.amesta.enums.Purchase;

public enum PaymentStatus {
    UNPAID("UNPAID"),
    PARTIALLY_PAID("PARTIALLY PAID"),
    PAID("PAID"),
    REFUNDED("REFUNDED");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static PaymentStatus fromString(String status) {
        for (PaymentStatus paymentStatus : PaymentStatus.values()) {
            if (paymentStatus.status.equalsIgnoreCase(status)) {
                return paymentStatus;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return status;
    }
}