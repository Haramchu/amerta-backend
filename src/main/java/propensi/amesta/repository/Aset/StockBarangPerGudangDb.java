package propensi.amesta.repository.Aset;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Aset.StockBarangPerGudang;

@Repository
public interface StockBarangPerGudangDb extends JpaRepository<StockBarangPerGudang, UUID> {
    
}
