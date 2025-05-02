package propensi.amesta.enums.Purchase;

public enum DeliveryStatus {
    PENDING("PENDING"),
    IN_DELIVERY("IN DELIVERY"),
    DELIVERED("DELIVERED"),
    CANCELLED("CANCELLED");

    private final String status;

    DeliveryStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static DeliveryStatus fromString(String status) {
        for (DeliveryStatus deliveryStatus : DeliveryStatus.values()) {
            if (deliveryStatus.status.equalsIgnoreCase(status)) {
                return deliveryStatus;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return status;
    }
}