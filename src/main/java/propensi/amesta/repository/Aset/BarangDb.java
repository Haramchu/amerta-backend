package propensi.amesta.repository.Aset;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.EndUser.User;

@Repository
public interface BarangDb extends JpaRepository<Barang, String> {
    List<Barang> findByKategori(String kategori);
    boolean existsByNamaAndMerk(String nama, String merk);
    List<Barang> findByMerk(String merk);
    List<Barang> findByNamaAndMerk(String nama, String merk);
    Optional<Barang> findByNama(String namaBarang);
}