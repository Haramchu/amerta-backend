package propensi.amesta.repository.Aset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Aset.Gudang;

@Repository
public interface GudangDb extends JpaRepository<Gudang, String> {

}