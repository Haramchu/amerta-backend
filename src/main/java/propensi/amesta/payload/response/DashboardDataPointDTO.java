package propensi.amesta.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DashboardDataPointDTO {
    private String x;      // label kategori atau waktu (hasil dari group by)
    private Double y;      // nilai hasil agregasi
}
