package propensi.amesta.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class FilterDashboardDTO {
    private LocalDate startDate;
    private LocalDate endDate;

    private List<String> gudangIds; // ID atau nama gudang
    private List<String> barangIds;
    private List<String> customerIds; // berlaku untuk supplier maupun customer
    private List<String> statusList;
}
