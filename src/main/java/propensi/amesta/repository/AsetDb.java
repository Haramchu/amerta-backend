package propensi.amesta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Aset;

@Repository
public interface AsetDb extends JpaRepository<Aset, String> {

}