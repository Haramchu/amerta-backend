package propensi.amesta.service.Aset;

import propensi.amesta.payload.request.TransferBarangRequestDTO;
import propensi.amesta.payload.response.TransferBarangResponseDTO;

public interface TransferBarangService {
    TransferBarangResponseDTO addTransferBarang(TransferBarangRequestDTO request);
    
}