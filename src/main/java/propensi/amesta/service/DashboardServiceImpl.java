package propensi.amesta.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import propensi.amesta.payload.request.DashboardRequestDTO;
import propensi.amesta.payload.request.FilterDashboardDTO;
import propensi.amesta.payload.response.DashboardDataPointDTO;
import propensi.amesta.payload.response.DashboardResponseDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DashboardResponseDTO getDynamicDashboardData(DashboardRequestDTO request) {
        String entityAlias = "e";
        StringBuilder jpql = new StringBuilder();
        List<String> whereClause = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        // Extract y field and join info
        JoinInfo joinInfo = extractJoinInfo(request.getY(), entityAlias);
        String yField = joinInfo.field; // e.g., "s.shippingFee"
        List<String> joins = joinInfo.joins; // e.g., ["JOIN e.shipping s"]

        // Build SELECT
        jpql.append("SELECT ");
        jpql.append(formatGroupByX(request.getX(), entityAlias)).append(", ");
        jpql.append(getAggregationFunction(request.getAggregation(), yField)).append(" ");
        jpql.append("FROM ").append(getEntityClassName(request.getEntity())).append(" ").append(entityAlias)
                .append(" ");

        // Apply JOIN if any
        for (String join : joins) {
            jpql.append(join).append(" ");
        }

        // WHERE clause: startDate, endDate, customer, status, barang, gudang
        FilterDashboardDTO filter = request.getFilter();
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
            if (filter.getBarangIds() != null && !filter.getBarangIds().isEmpty()) {
                whereClause.add("i.barang.id IN ?" + paramIndex);
                parameters.add(filter.getBarangIds());
                paramIndex++;
            }
            if (filter.getGudangIds() != null && !filter.getGudangIds().isEmpty()) {
                whereClause.add("i.gudangTujuan.nama IN ?" + paramIndex);
                parameters.add(filter.getGudangIds());
                paramIndex++;
            }
        }

        if (!whereClause.isEmpty()) {
            jpql.append("WHERE ").append(String.join(" AND ", whereClause)).append(" ");
        }

        // GROUP BY
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

}
