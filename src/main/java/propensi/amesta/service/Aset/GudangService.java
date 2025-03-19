package propensi.amesta.service.Aset;

import java.util.List;

import propensi.amesta.payload.request.AlamatGudangRequestDTO;
import propensi.amesta.payload.request.GudangRequestDTO;
import propensi.amesta.payload.response.GudangResponseDTO;

public interface GudangService {
    GudangResponseDTO addGudang(GudangRequestDTO gudangRequestDTO, AlamatGudangRequestDTO alamatGudangRequestDTO);
    List<GudangResponseDTO> getAllGudang();
    GudangResponseDTO getGudangByName(String namaGudang);
    GudangResponseDTO updateGudang(String namaGudang, GudangRequestDTO gudangRequestDTO, AlamatGudangRequestDTO alamatGudangRequestDTO);
}