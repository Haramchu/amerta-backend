package propensi.amesta.repository.Finance;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Finance.Pembelian;

@Repository
public interface PembelianDb extends JpaRepository<Pembelian, UUID> {

}
