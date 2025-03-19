package propensi.amesta.service.Aset;

import java.util.List;

import propensi.amesta.payload.request.BarangRequestDTO;
import propensi.amesta.payload.request.UpdateBarangRequestDTO;
import propensi.amesta.payload.response.BarangResponseDTO;

public interface BarangService {
    BarangResponseDTO addBarang(BarangRequestDTO barangRequestDTO);
    BarangResponseDTO getBarangById(String id);
    BarangResponseDTO updateBarang(String id, UpdateBarangRequestDTO barangRequestDTO);
    List<BarangResponseDTO> getBarangByKategori(String kategori);
    List<BarangResponseDTO> getBarangByMerk(String merk);
    List<BarangResponseDTO> getAllBarang();
    BarangResponseDTO changeStatusBarang(String id);
}
