package propensi.amesta.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import propensi.amesta.model.Penjualan;

@Repository
public interface PenjualanDb extends JpaRepository<Penjualan, UUID> {

}
