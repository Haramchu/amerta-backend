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

    // ✅ Menambahkan penerimaan baru
    @Override
    public Penerimaan createPenerimaan(Penerimaan penerimaan) {
        return penerimaanRepository.save(penerimaan);
    }

    // ✅ Mengambil semua penerimaan
    @Override
    public List<Penerimaan> getAllPenerimaan() {
        return penerimaanRepository.findAll();
    }

    // ✅ Mengambil penerimaan berdasarkan ID
    @Override
    public Optional<Penerimaan> getPenerimaanById(UUID id) {
        return penerimaanRepository.findById(id);
    }

    // ✅ Mengupdate penerimaan berdasarkan ID
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

    // ✅ Menghapus penerimaan berdasarkan ID
    @Override
    public void deletePenerimaan(UUID id) {
        if (!penerimaanRepository.existsById(id)) {
            throw new RuntimeException("Penerimaan tidak ditemukan");
        }
        penerimaanRepository.deleteById(id);
    }
}
