package propensi.amesta.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import propensi.amesta.payload.request.DashboardRequestDTO;
import propensi.amesta.payload.request.FilterDashboardDTO;
import propensi.amesta.payload.response.DashboardDataPointDTO;
import propensi.amesta.payload.response.DashboardResponseDTO;

import java.nio.file.DirectoryStream.Filter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DashboardResponseDTO getDynamicDashboardData(DashboardRequestDTO request) {
        if (request.getEntity().equalsIgnoreCase("barang")) {
            return aggregateByBarang(request.getFilter());
        } else if (request.getEntity().equalsIgnoreCase("gudang")) {
            return aggregateByGudang(request.getFilter());
        } else if (request.getEntity().equalsIgnoreCase("status")) {
            return aggregateByStatus(request.getFilter());
        }

        String entityAlias = "e";
        StringBuilder jpql = new StringBuilder();
        List<String> whereClause = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        // Extract y field and join info
        JoinInfo joinInfo = extractJoinInfo(request.getY(), entityAlias);
        String yField = joinInfo.field;
        List<String> joins = new ArrayList<>(joinInfo.joins);

        // Cek apakah perlu join ke items
        boolean isOrderEntity = request.getEntity().equalsIgnoreCase("sales_order")
                || request.getEntity().equalsIgnoreCase("purchase_order");

        FilterDashboardDTO filter = request.getFilter();
        boolean filteringByBarang = filter != null && filter.getBarangIds() != null && !filter.getBarangIds().isEmpty();
        boolean filteringByGudang = filter != null && filter.getGudangIds() != null && !filter.getGudangIds().isEmpty();

        if (isOrderEntity && (filteringByBarang || filteringByGudang)) {
            joins.add("JOIN " + entityAlias + ".items i");
        }

        // Build SELECT
        jpql.append("SELECT ")
                .append(formatGroupByX(request.getX(), entityAlias)).append(", ")
                .append(getAggregationFunction(request.getAggregation(), yField)).append(" ")
                .append("FROM ").append(getEntityClassName(request.getEntity())).append(" ").append(entityAlias)
                .append(" ");

        for (String join : joins) {
            jpql.append(join).append(" ");
        }

        // WHERE
        int paramIndex = 1;
        if (filter != null) {
            if (filter.getStartDate() != null && filter.getEndDate() != null) {
                whereClause.add(
                        entityAlias + "." + request.getX() + " BETWEEN ?" + paramIndex + " AND ?" + (paramIndex + 1));
                parameters.add(filter.getStartDate());
                parameters.add(filter.getEndDate());
                paramIndex += 2;
            }
            if (filter.getCustomerIds() != null && !filter.getCustomerIds().isEmpty()) {
                whereClause.add(entityAlias + ".customer.id IN ?" + paramIndex);
                parameters.add(filter.getCustomerIds());
                paramIndex++;
            }
            if (filter.getStatusList() != null && !filter.getStatusList().isEmpty()) {
                whereClause.add(entityAlias + ".status IN ?" + paramIndex);
                parameters.add(filter.getStatusList());
                paramIndex++;
            }
            if (filteringByBarang) {
                whereClause.add("i.barang.id IN ?" + paramIndex);
                parameters.add(filter.getBarangIds());
                paramIndex++;
            }
            if (filteringByGudang) {
                whereClause.add("i.gudangTujuan.nama IN ?" + paramIndex);
                parameters.add(filter.getGudangIds());
                paramIndex++;
            }
        }

        if (!whereClause.isEmpty()) {
            jpql.append("WHERE ").append(String.join(" AND ", whereClause)).append(" ");
        }

        // GROUP BY dan ORDER
        jpql.append("GROUP BY ").append(formatGroupByX(request.getX(), entityAlias)).append(" ");
        jpql.append("ORDER BY 1");

        // Execute
        Query query = entityManager.createQuery(jpql.toString());
        for (int i = 0; i < parameters.size(); i++) {
            query.setParameter(i + 1, parameters.get(i));
        }

        List<Object[]> results = query.getResultList();
        List<DashboardDataPointDTO> dataPoints = new ArrayList<>();
        for (Object[] row : results) {
            dataPoints.add(new DashboardDataPointDTO(
                    row[0].toString(),
                    row[1] != null ? Double.parseDouble(row[1].toString()) : 0.0));
        }

        DashboardResponseDTO response = new DashboardResponseDTO();
        response.setEntity(request.getEntity());
        response.setX(request.getX());
        response.setY(request.getY());
        response.setAggregation(request.getAggregation());
        response.setData(dataPoints);

        return response;
    }

    private String getAggregationFunction(String aggregation, String field) {
        return switch (aggregation.toLowerCase()) {
            case "sum" -> "SUM(" + field + ")";
            case "avg" -> "AVG(" + field + ")";
            case "count" -> "COUNT(" + field + ")";
            case "min" -> "MIN(" + field + ")";
            case "max" -> "MAX(" + field + ")";
            default -> throw new IllegalArgumentException("Aggregation tidak dikenali: " + aggregation);
        };
    }

    private String getEntityClassName(String entityKeyword) {
        return switch (entityKeyword.toLowerCase()) {
            case "purchase_invoice" -> "PurchaseInvoice";
            case "purchase_order" -> "PurchaseOrder";
            case "purchase_order_item" -> "PurchaseOrderItem";
            case "purchase_payment" -> "PurchasePayment";
            case "delivery" -> "Delivery";
            case "sales_invoice" -> "SalesInvoice";
            case "sales_order" -> "SalesOrder";
            case "sales_order_item" -> "SalesOrderItem";
            case "sales_payment" -> "SalesPayment";
            case "shipping" -> "Shipping";
            default -> throw new IllegalArgumentException("Entity tidak dikenali: " + entityKeyword);
        };
    }

    private String formatGroupByX(String xField, String entityAlias) {
        return "FUNCTION('TO_CHAR', " + entityAlias + "." + xField + ", 'YYYY-MM')";
    }

    private JoinInfo extractJoinInfo(String rawField, String entityAlias) {
        List<String> joins = new ArrayList<>();

        if (!rawField.contains(".")) {
            return new JoinInfo(entityAlias + "." + rawField, joins);
        }

        String[] parts = rawField.split("\\.");
        String relation = parts[0]; // e.g., shipping
        String field = parts[1]; // e.g., shippingFee

        String alias = switch (relation) {
            case "shipping" -> "s";
            case "invoice" -> "i";
            case "payment" -> "p";
            case "receipt" -> "r";
            case "delivery" -> "d";
            default -> throw new IllegalArgumentException("Relasi tidak dikenali: " + relation);
        };

        joins.add("JOIN " + entityAlias + "." + relation + " " + alias);
        return new JoinInfo(alias + "." + field, joins);
    }

    private DashboardResponseDTO aggregateByBarang(FilterDashboardDTO filter) {
        StringBuilder queryStr = new StringBuilder("""
                    SELECT i.barang.nama, SUM(i.quantity)
                    FROM SalesOrder so JOIN so.items i
                """);

        List<String> whereClause = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        if (filter != null) {
            if (filter.getStartDate() != null && filter.getEndDate() != null) {
                whereClause.add("so.salesDate BETWEEN ?" + paramIndex + " AND ?" + (paramIndex + 1));
                params.add(filter.getStartDate());
                params.add(filter.getEndDate());
                paramIndex += 2;
            }

            if (filter.getGudangIds() != null && !filter.getGudangIds().isEmpty()) {
                whereClause.add("i.gudangTujuan.nama IN ?" + paramIndex);
                params.add(filter.getGudangIds());
                paramIndex++;
            }

            if (filter.getBarangIds() != null && !filter.getBarangIds().isEmpty()) {
                whereClause.add("i.barang.id IN ?" + paramIndex);
                params.add(filter.getBarangIds());
                paramIndex++;
            }
        }

        if (!whereClause.isEmpty()) {
            queryStr.append(" WHERE ").append(String.join(" AND ", whereClause));
        }

        queryStr.append(" GROUP BY i.barang.nama ORDER BY SUM(i.quantity) DESC");

        Query query = entityManager.createQuery(queryStr.toString());
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        List<Object[]> results = query.getResultList();
        List<DashboardDataPointDTO> data = new ArrayList<>();
        for (Object[] row : results) {
            data.add(new DashboardDataPointDTO(row[0].toString(), ((Number) row[1]).doubleValue()));
        }

        DashboardResponseDTO response = new DashboardResponseDTO();
        response.setEntity("barang");
        response.setX("nama");
        response.setY("quantity");
        response.setAggregation("sum");
        response.setData(data);
        return response;
    }

    private DashboardResponseDTO aggregateByGudang(FilterDashboardDTO filter) {
        StringBuilder queryStr = new StringBuilder("""
                    SELECT i.gudangTujuan.nama, SUM(i.quantity)
                    FROM SalesOrder so JOIN so.items i
                """);

        List<String> whereClause = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        if (filter != null) {
            if (filter.getStartDate() != null && filter.getEndDate() != null) {
                whereClause.add("so.salesDate BETWEEN ?" + paramIndex + " AND ?" + (paramIndex + 1));
                params.add(filter.getStartDate());
                params.add(filter.getEndDate());
                paramIndex += 2;
            }

            if (filter.getBarangIds() != null && !filter.getBarangIds().isEmpty()) {
                whereClause.add("i.barang.id IN ?" + paramIndex);
                params.add(filter.getBarangIds());
                paramIndex++;
            }

            if (filter.getGudangIds() != null && !filter.getGudangIds().isEmpty()) {
                whereClause.add("i.gudangTujuan.nama IN ?" + paramIndex);
                params.add(filter.getGudangIds());
                paramIndex++;
            }
        }

        if (!whereClause.isEmpty()) {
            queryStr.append(" WHERE ").append(String.join(" AND ", whereClause));
        }

        queryStr.append(" GROUP BY i.gudangTujuan.nama ORDER BY SUM(i.quantity) DESC");

        Query query = entityManager.createQuery(queryStr.toString());
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        List<Object[]> results = query.getResultList();
        List<DashboardDataPointDTO> data = new ArrayList<>();
        for (Object[] row : results) {
            data.add(new DashboardDataPointDTO(
                    row[0] != null ? row[0].toString() : "Unknown",
                    row[1] != null ? ((Number) row[1]).doubleValue() : 0.0));
        }

        DashboardResponseDTO response = new DashboardResponseDTO();
        response.setEntity("gudang");
        response.setX("nama");
        response.setY("quantity");
        response.setAggregation("sum");
        response.setData(data);
        return response;
    }

    private DashboardResponseDTO aggregateByStatus(FilterDashboardDTO filter) {
        StringBuilder queryStr = new StringBuilder("""
                    SELECT so.status, COUNT(so)
                    FROM SalesOrder so
                """);

        List<String> whereClause = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        if (filter != null && filter.getStartDate() != null && filter.getEndDate() != null) {
            whereClause.add("so.salesDate BETWEEN ?" + paramIndex + " AND ?" + (paramIndex + 1));
            params.add(filter.getStartDate());
            params.add(filter.getEndDate());
            paramIndex += 2;
        }

        if (!whereClause.isEmpty()) {
            queryStr.append(" WHERE ").append(String.join(" AND ", whereClause));
        }

        queryStr.append(" GROUP BY so.status");

        Query query = entityManager.createQuery(queryStr.toString());
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        List<Object[]> results = query.getResultList();
        List<DashboardDataPointDTO> data = new ArrayList<>();
        for (Object[] row : results) {
            data.add(new DashboardDataPointDTO(row[0].toString(), ((Number) row[1]).doubleValue()));
        }

        DashboardResponseDTO response = new DashboardResponseDTO();
        response.setEntity("status");
        response.setX("status");
        response.setY("count");
        response.setAggregation("count");
        response.setData(data);
        return response;
    }

}
