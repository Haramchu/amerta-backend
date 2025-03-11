package propensi.amesta.repository.Aset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Aset.Barang;

@Repository
public interface BarangDb extends JpaRepository<Barang, String> {

}