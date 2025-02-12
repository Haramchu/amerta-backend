package propensi.amesta.repository.EndUser;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.EndUser.Direktur;

@Repository
public interface DirekturDb extends JpaRepository<Direktur, UUID> {
    
}
