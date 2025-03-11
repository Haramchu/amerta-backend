package propensi.amesta.repository.Aset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Aset.TransferBarang;

@Repository
public interface TransferBarangDb extends JpaRepository<TransferBarang, String> {

}