package propensi.amesta.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardResponseDTO {
    private String entity;
    private String x;
    private String y;
    private String aggregation;
    private List<DashboardDataPointDTO> data;
}
