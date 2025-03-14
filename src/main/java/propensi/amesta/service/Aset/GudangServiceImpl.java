package propensi.amesta.service.Aset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.amesta.payload.request.GudangRequestDTO;
import propensi.amesta.payload.response.GudangResponseDTO;
import propensi.amesta.model.Aset.Gudang;
import propensi.amesta.model.Aset.AlamatGudang;
import propensi.amesta.model.EndUser.KepalaGudang;
import propensi.amesta.repository.Aset.GudangDb;
import propensi.amesta.repository.EndUser.KepalaGudangDb;
import propensi.amesta.repository.Aset.AlamatGudangDb;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GudangServiceImpl implements GudangService {

    @Autowired
    private GudangDb gudangDb;

    @Autowired
    private KepalaGudangDb kepalaGudangDb;

    @Autowired
    private AlamatGudangDb alamatGudangDb;

    @Override
    public GudangResponseDTO addGudang(GudangRequestDTO gudangRequestDTO) {
        KepalaGudang kepalaGudang = kepalaGudangDb.findById(gudangRequestDTO.getKepalaGudangId())
            .orElseThrow(() -> new RuntimeException("Kepala Gudang not found"));

        AlamatGudang alamatGudang = new AlamatGudang();
        alamatGudang.setAlamat(gudangRequestDTO.getAlamat());
        alamatGudang.setKota(gudangRequestDTO.getKota());
        alamatGudang.setProvinsi(gudangRequestDTO.getProvinsi());
        alamatGudang.setKodePos(gudangRequestDTO.getKodePos());
        alamatGudangDb.save(alamatGudang);

        Gudang gudang = new Gudang();
        gudang.setNama(gudangRequestDTO.getNama());
        gudang.setDeskripsi(gudangRequestDTO.getDeskripsi());
        gudang.setKapasitas(gudangRequestDTO.getKapasitas());
        gudang.setKepalaGudang(kepalaGudang);
        gudang.setAlamatGudang(alamatGudang);

        gudangDb.save(gudang);

        return new GudangResponseDTO(gudang);
    }

    @Override
    public List<GudangResponseDTO> getAllGudang() {
        List<Gudang> gudangList = gudangDb.findAll();

        return gudangList.stream()
                         .map(GudangResponseDTO::new)
                         .collect(Collectors.toList());
    }

    @Override
    public GudangResponseDTO getGudangByName(String namaGudang) {
        Gudang gudang = gudangDb.findById(namaGudang)
            .orElseThrow(() -> new RuntimeException("Gudang not found"));

        return new GudangResponseDTO(gudang);
    }

    @Override
    public GudangResponseDTO updateGudang(String namaGudang, GudangRequestDTO gudangRequestDTO) {
        Gudang gudang = gudangDb.findById(namaGudang)
            .orElseThrow(() -> new RuntimeException("Gudang not found"));

        gudang.setNama(gudangRequestDTO.getNama());
        gudang.setDeskripsi(gudangRequestDTO.getDeskripsi());
        gudang.setKapasitas(gudangRequestDTO.getKapasitas());

        AlamatGudang alamatGudang = gudang.getAlamatGudang();
        alamatGudang.setAlamat(gudangRequestDTO.getAlamat());
        alamatGudang.setKota(gudangRequestDTO.getKota());
        alamatGudang.setProvinsi(gudangRequestDTO.getProvinsi());
        alamatGudang.setKodePos(gudangRequestDTO.getKodePos());
        alamatGudangDb.save(alamatGudang);

        gudangDb.save(gudang);

        return new GudangResponseDTO(gudang);
    }
}