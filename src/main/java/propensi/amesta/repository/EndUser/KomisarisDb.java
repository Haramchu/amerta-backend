package propensi.amesta.repository.EndUser;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.EndUser.KepalaGudang;

@Repository
public interface KomisarisDb extends JpaRepository<KepalaGudang, UUID> {
    
}
