package propensi.amesta.enums.Sales;

public enum SalesOrderStatus {
    CREATED("CREATED"),
    INVOICED("INVOICED"),
    PAID("PAID"),
    SHIPPED("SHIPPED"),
    COMPLETED("COMPLETED");
    
    private final String status;

    SalesOrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static SalesOrderStatus fromString(String status) {
        for (SalesOrderStatus orderStatus : SalesOrderStatus.values()) {
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