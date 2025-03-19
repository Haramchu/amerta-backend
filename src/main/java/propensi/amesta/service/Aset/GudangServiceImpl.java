package propensi.amesta.service.Aset;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import propensi.amesta.model.Aset.AlamatGudang;
import propensi.amesta.model.Aset.Gudang;
import propensi.amesta.model.Aset.StockBarangPerGudang;
import propensi.amesta.model.EndUser.KepalaGudang;
import propensi.amesta.payload.request.AlamatGudangRequestDTO;
import propensi.amesta.payload.request.GudangRequestDTO;
import propensi.amesta.payload.response.AlamatGudangResponseDTO;
import propensi.amesta.payload.response.BarangResponseDTO;
import propensi.amesta.payload.response.GudangResponseDTO;
import propensi.amesta.payload.response.KepalaGudangResponseDTO;
import propensi.amesta.repository.Aset.GudangDb;
import propensi.amesta.repository.EndUser.KepalaGudangDb;

@Service
public class GudangServiceImpl implements GudangService {

    @Autowired
    private GudangDb gudangDb;

    @Autowired
    private KepalaGudangDb kepalaGudangDb;

    @Autowired
    private BarangServiceImpl barangService;

    @Override
    public GudangResponseDTO addGudang(GudangRequestDTO gudangRequestDTO, AlamatGudangRequestDTO alamatGudangRequestDTO) {
        KepalaGudang kepalaGudang = null;
        if (gudangRequestDTO.getKepalaGudangId() != null) {
            kepalaGudang = kepalaGudangDb.findById(gudangRequestDTO.getKepalaGudangId())
                .orElseThrow(() -> new RuntimeException("Kepala Gudang tidak ditemukan"));
        }

        AlamatGudang alamatGudang = new AlamatGudang();
        alamatGudang.setAlamat(alamatGudangRequestDTO.getAlamat());
        alamatGudang.setKota(alamatGudangRequestDTO.getKota());
        alamatGudang.setProvinsi(alamatGudangRequestDTO.getProvinsi());
        alamatGudang.setKodePos(alamatGudangRequestDTO.getKodePos());

        Gudang gudang = new Gudang();
        gudang.setNama(gudangRequestDTO.getNama());
        gudang.setDeskripsi(gudangRequestDTO.getDeskripsi());
        gudang.setKapasitas(gudangRequestDTO.getKapasitas());
        gudang.setKepalaGudang(kepalaGudang);
        gudang.setAlamatGudang(alamatGudang);
        gudang.setCreatedDate(new Date());
        gudang.setUpdatedDate(new Date());
        alamatGudang.setGudang(gudang);

        gudangDb.save(gudang);

        return gudangToGudangResponseDTO(gudang);
    }

    @Override
    public List<GudangResponseDTO> getAllGudang() {
        List<Gudang> gudangList = gudangDb.findAll();

        return gudangList.stream()
                         .map(this::gudangToGudangResponseDTO)
                         .toList();
    }

    @Override
    public GudangResponseDTO getGudangByName(String namaGudang) {
        Gudang gudang = gudangDb.findById(namaGudang)
            .orElseThrow(() -> new RuntimeException("Gudang tidak ditemukan"));

        return gudangToGudangResponseDTO(gudang);
    }

    @Override
    public GudangResponseDTO updateGudang(String namaGudang, GudangRequestDTO gudangRequestDTO, AlamatGudangRequestDTO alamatGudangRequestDTO) {
        Gudang gudang = gudangDb.findById(namaGudang)
            .orElseThrow(() -> new RuntimeException("Gudang tidak ditemukan"));

        gudang.setNama(gudangRequestDTO.getNama());
        gudang.setDeskripsi(gudangRequestDTO.getDeskripsi());
        gudang.setKapasitas(gudangRequestDTO.getKapasitas());
        
        
        if (gudangRequestDTO.getKepalaGudangId() != null) {
            KepalaGudang kepalaGudang = kepalaGudangDb.findById(gudangRequestDTO.getKepalaGudangId())
                .orElseThrow(() -> new RuntimeException("Kepala Gudang tidak ditemukan"));
            gudang.setKepalaGudang(kepalaGudang);
        }
        
        AlamatGudang alamatGudang = gudang.getAlamatGudang();
        alamatGudang.setAlamat(alamatGudangRequestDTO.getAlamat());
        alamatGudang.setKota(alamatGudangRequestDTO.getKota());
        alamatGudang.setProvinsi(alamatGudangRequestDTO.getProvinsi());
        alamatGudang.setKodePos(alamatGudangRequestDTO.getKodePos());

        gudangDb.save(gudang);

        return gudangToGudangResponseDTO(gudang);
    }

    public GudangResponseDTO gudangToGudangResponseDTO(Gudang gudang) {
        KepalaGudang kepalaGudang = gudang.getKepalaGudang();
        AlamatGudang alamatGudang = gudang.getAlamatGudang();
        
        KepalaGudangResponseDTO kepalaGudangDTO = null;
        if (kepalaGudang != null) {
            kepalaGudangDTO = new KepalaGudangResponseDTO(
                kepalaGudang.getId(),
                kepalaGudang.getName(),
                kepalaGudang.getUsername(),
                kepalaGudang.getEmail()
            );
        }
        
        AlamatGudangResponseDTO alamatGudangDTO = null;
        if (alamatGudang != null) {
            alamatGudangDTO = new AlamatGudangResponseDTO(
                alamatGudang.getId(),
                alamatGudang.getAlamat(),
                alamatGudang.getKota(),
                alamatGudang.getProvinsi(),
                alamatGudang.getKodePos()
            );
        }

        List<BarangResponseDTO> listBarangDTO = new ArrayList<>();
        if (gudang.getListBarang() != null && !gudang.getListBarang().isEmpty()) {
            for (StockBarangPerGudang stockBarang : gudang.getListBarang()) {
                BarangResponseDTO barangDTO = barangService.barangToBarangResponseDTO(stockBarang.getBarang());
                listBarangDTO.add(barangDTO);
            }
        }
        
        return new GudangResponseDTO(
            gudang.getNama(),
            gudang.getDeskripsi(),
            gudang.getKapasitas(),
            kepalaGudangDTO,
            alamatGudangDTO,
            listBarangDTO,
            gudang.getCreatedDate(),
            gudang.getUpdatedDate()
        );
    }

    public KepalaGudangResponseDTO kepalaGudangToKepalaGudangResponseDTO(KepalaGudang kepalaGudang) {
        return new KepalaGudangResponseDTO(
            kepalaGudang.getId(),
            kepalaGudang.getName(),
            kepalaGudang.getUsername(),
            kepalaGudang.getEmail()
        );
    }

    public AlamatGudangResponseDTO alamatGudangToAlamatGudangResponseDTO(AlamatGudang alamatGudang) {
        return new AlamatGudangResponseDTO(
            alamatGudang.getId(),
            alamatGudang.getAlamat(),
            alamatGudang.getKota(),
            alamatGudang.getProvinsi(),
            alamatGudang.getKodePos()
        );
    }
}