package propensi.amesta.service;

import propensi.amesta.model.Finance.Penerimaan;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PenerimaanService {

    Penerimaan createPenerimaan(Penerimaan penerimaan);

    List<Penerimaan> getAllPenerimaan();

    Optional<Penerimaan> getPenerimaanById(UUID id);

    Penerimaan updatePenerimaan(UUID id, Penerimaan newData);

    void deletePenerimaan(UUID id);
}
