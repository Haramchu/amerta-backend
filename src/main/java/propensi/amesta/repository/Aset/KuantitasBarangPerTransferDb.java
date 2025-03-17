package propensi.amesta.repository.Aset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import propensi.amesta.model.Aset.KuantitasBarangPerTransfer;

import java.util.UUID;

@Repository
public interface KuantitasBarangPerTransferDb extends JpaRepository<KuantitasBarangPerTransfer, UUID> {
}
