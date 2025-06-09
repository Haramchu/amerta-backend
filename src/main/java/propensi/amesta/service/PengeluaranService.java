package propensi.amesta.service;

import propensi.amesta.model.Finance.Pengeluaran;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PengeluaranService {

    Pengeluaran createPengeluaran(Pengeluaran pengeluaran);

    List<Pengeluaran> getAllPengeluaran();

    Optional<Pengeluaran> getPengeluaranById(UUID id);

    Pengeluaran updatePengeluaran(UUID id, Pengeluaran newData);

    void deletePengeluaran(UUID id);
}
