package propensi.amesta.repository.Finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import propensi.amesta.model.Finance.Penerimaan;

import java.util.UUID;

@Repository
public interface PenerimaanDb extends JpaRepository<Penerimaan, UUID> {
    
}
