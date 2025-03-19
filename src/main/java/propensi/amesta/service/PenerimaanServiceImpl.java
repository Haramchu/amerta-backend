package propensi.amesta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.amesta.model.Finance.Penerimaan;
import propensi.amesta.repository.Finance.PenerimaanDb;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PenerimaanServiceImpl implements PenerimaanService {

    @Autowired
    private PenerimaanDb penerimaanRepository;

    @Override
    public Penerimaan createPenerimaan(Penerimaan penerimaan) {
        return penerimaanRepository.save(penerimaan);
    }

    @Override
    public List<Penerimaan> getAllPenerimaan() {
        return penerimaanRepository.findAll();
    }

    @Override
    public Optional<Penerimaan> getPenerimaanById(UUID id) {
        return penerimaanRepository.findById(id);
    }

    @Override
    public Penerimaan updatePenerimaan(UUID id, Penerimaan newData) {
        return penerimaanRepository.findById(id).map(penerimaan -> {
            penerimaan.setJenisPenerimaan(newData.getJenisPenerimaan());
            penerimaan.setJumlah(newData.getJumlah());
            penerimaan.setTanggal(newData.getTanggal());
            penerimaan.setSumberPenerimaan(newData.getSumberPenerimaan());
            penerimaan.setKeterangan(newData.getKeterangan());

            return penerimaanRepository.save(penerimaan);
        }).orElseThrow(() -> new RuntimeException("Penerimaan tidak ditemukan"));
    }

    @Override
    public void deletePenerimaan(UUID id) {
        if (!penerimaanRepository.existsById(id)) {
            throw new RuntimeException("Penerimaan tidak ditemukan");
        }
        penerimaanRepository.deleteById(id);
    }
}
