package propensi.amesta.enums.Purchase;

public enum PurchaseOrderStatus {
    CREATED("CREATED"),
    CONFIRMED("CONFIRMED"),
    PAID("PAID"),
    IN_DELIVERY("IN DELIVERY"),
    COMPLETED("COMPLETED");

    private final String status;

    PurchaseOrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static PurchaseOrderStatus fromString(String status) {
        for (PurchaseOrderStatus orderStatus : PurchaseOrderStatus.values()) {
            if (orderStatus.status.equalsIgnoreCase(status)) {
                return orderStatus;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return status;
    }
}