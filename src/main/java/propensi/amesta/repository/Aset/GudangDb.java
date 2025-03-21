package propensi.amesta.repository.Aset;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Aset.Gudang;

@Repository
public interface GudangDb extends JpaRepository<Gudang, String> {

    @Query("SELECT g FROM Gudang g WHERE LOWER(g.nama) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Gudang> findByNamaContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    @Query("SELECT g FROM Gudang g JOIN g.alamatGudang a WHERE LOWER(a.kota) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Gudang> findByAlamatGudangKotaContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    @Query("SELECT g FROM Gudang g JOIN g.alamatGudang a WHERE LOWER(a.provinsi) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Gudang> findByAlamatGudangProvinsiContainingIgnoreCase(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT DISTINCT g FROM Gudang g JOIN g.alamatGudang a WHERE " +
           "LOWER(g.nama) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.kota) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.provinsi) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Gudang> findByNameOrCityOrProvince(@Param("searchTerm") String searchTerm);
}