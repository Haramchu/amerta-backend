package propensi.amesta.service.Aset;

import java.util.List;

import propensi.amesta.payload.request.Aset.TransferBarangRequestDTO;
import propensi.amesta.payload.response.Aset.TransferBarangResponseDTO;

public interface TransferBarangService {
    TransferBarangResponseDTO addTransferBarang(TransferBarangRequestDTO request);
    List<TransferBarangResponseDTO> getAllTransferBarang();
    TransferBarangResponseDTO getTransferBarangByID(String id);
    
}