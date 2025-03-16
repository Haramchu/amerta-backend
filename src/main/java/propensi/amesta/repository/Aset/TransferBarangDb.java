package propensi.amesta.repository.Aset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Aset.TransferBarang;

@Repository
public interface TransferBarangDb extends JpaRepository<TransferBarang, String> {

    @Query("SELECT COUNT(t) FROM TransferBarang t WHERE EXTRACT(YEAR FROM t.tanggalPemindahan) = ?1 AND EXTRACT(MONTH FROM t.tanggalPemindahan) = ?2")
    int countByYearAndMonth(int year, int month);

}