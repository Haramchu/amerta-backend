package propensi.amesta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Stok;

@Repository
public interface StokDb extends JpaRepository<Stok, String> {

}
