package propensi.amesta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.amesta.model.Finance.Pengeluaran;
import propensi.amesta.repository.Finance.PengeluaranDb;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PengeluaranServiceImpl implements PengeluaranService {

    @Autowired
    private PengeluaranDb pengeluaranRepository;

    @Override
    public Pengeluaran createPengeluaran(Pengeluaran pengeluaran) {
        return pengeluaranRepository.save(pengeluaran);
    }

    @Override
    public List<Pengeluaran> getAllPengeluaran() {
        return pengeluaranRepository.findAll();
    }

    @Override
    public Optional<Pengeluaran> getPengeluaranById(UUID id) {
        return pengeluaranRepository.findById(id);
    }

    @Override
    public Pengeluaran updatePengeluaran(UUID id, Pengeluaran newData) {
        return pengeluaranRepository.findById(id).map(pengeluaran -> {
            pengeluaran.setJenisPengeluaran(newData.getJenisPengeluaran());
            pengeluaran.setJumlah(newData.getJumlah());
            pengeluaran.setTanggal(newData.getTanggal());
            pengeluaran.setPenanggung_jawab(newData.getPenanggung_jawab());
            pengeluaran.setKeterangan(newData.getKeterangan());

            return pengeluaranRepository.save(pengeluaran);
        }).orElseThrow(() -> new RuntimeException("Pengeluaran tidak ditemukan"));
    }

    @Override
    public void deletePengeluaran(UUID id) {
        if (!pengeluaranRepository.existsById(id)) {
            throw new RuntimeException("Pengeluaran tidak ditemukan");
        }
        pengeluaranRepository.deleteById(id);
    }
}
