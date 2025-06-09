package propensi.amesta.repository.Aset;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.Aset.Gudang;
import propensi.amesta.model.Aset.StockBarangPerGudang;

@Repository
public interface StockBarangPerGudangDb extends JpaRepository<StockBarangPerGudang, UUID> {

    Optional<StockBarangPerGudang> findByBarangAndGudang(Barang barang, Gudang gudang);
    
}
