package propensi.amesta.service.Aset;

import java.util.List;

import propensi.amesta.payload.request.Aset.AlamatGudangRequestDTO;
import propensi.amesta.payload.request.Aset.GudangRequestDTO;
import propensi.amesta.payload.response.Aset.GudangResponseDTO;

public interface GudangService {
    GudangResponseDTO addGudang(GudangRequestDTO gudangRequestDTO, AlamatGudangRequestDTO alamatGudangRequestDTO);
    List<GudangResponseDTO> getAllGudang();
    List<GudangResponseDTO> filterGudang(String keywords);
    GudangResponseDTO getGudangByName(String namaGudang);
    GudangResponseDTO updateGudang(String namaGudang, GudangRequestDTO gudangRequestDTO, AlamatGudangRequestDTO alamatGudangRequestDTO);
}