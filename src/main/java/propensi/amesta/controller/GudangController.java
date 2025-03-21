package propensi.amesta.controller;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import propensi.amesta.payload.request.AlamatGudangRequestDTO;
import propensi.amesta.payload.request.GudangRequestDTO;
import propensi.amesta.payload.response.BaseResponseDTO;
import propensi.amesta.payload.response.GudangResponseDTO;
import propensi.amesta.service.Aset.GudangService;

@RestController
@RequestMapping("/api/gudang")
public class GudangController {
    @Autowired
    private GudangService gudangService;
    
    @PostMapping("/add")
    public ResponseEntity<BaseResponseDTO<GudangResponseDTO>> createGudang(
        @Valid @RequestBody GudangRequestDTO gudangRequestDTO
    ) {
        AlamatGudangRequestDTO alamatGudangRequestDTO = gudangRequestDTO.getAlamatGudang();
        
        GudangResponseDTO newGudang = gudangService.addGudang(gudangRequestDTO, alamatGudangRequestDTO);
        BaseResponseDTO<GudangResponseDTO> response = new BaseResponseDTO<>(
            HttpStatus.CREATED.value(),
            "Gudang berhasil dibuat!",
            new Date(),
            newGudang
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/")
    public ResponseEntity<BaseResponseDTO<List<GudangResponseDTO>>> getAllGudang(
        @RequestParam(required = false) String search
    ) {
        List<GudangResponseDTO> gudangList;
        
        if (search != null && !search.isEmpty()) {
            gudangList = gudangService.filterGudang(search);
        } else {
            gudangList = gudangService.getAllGudang();
        }
        
        BaseResponseDTO<List<GudangResponseDTO>> response = new BaseResponseDTO<>(
            HttpStatus.OK.value(),
            "Daftar gudang berhasil diambil!",
            new Date(),
            gudangList
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @GetMapping("/{namaGudang}")
    public ResponseEntity<BaseResponseDTO<GudangResponseDTO>> getGudangByName(@PathVariable String namaGudang) {
        GudangResponseDTO gudang = gudangService.getGudangByName(namaGudang);
        BaseResponseDTO<GudangResponseDTO> response = new BaseResponseDTO<>(
            HttpStatus.OK.value(),
            "Gudang berhasil ditemukan!",
            new Date(),
            gudang
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @PutMapping("/update/{namaGudang}")
    public ResponseEntity<BaseResponseDTO<GudangResponseDTO>> updateGudang(
        @PathVariable String namaGudang,
        @Valid @RequestBody GudangRequestDTO gudangRequestDTO
    ) {
        AlamatGudangRequestDTO alamatGudangRequestDTO = gudangRequestDTO.getAlamatGudang();
        
        GudangResponseDTO updatedGudang = gudangService.updateGudang(namaGudang, gudangRequestDTO, alamatGudangRequestDTO);
        BaseResponseDTO<GudangResponseDTO> response = new BaseResponseDTO<>(
            HttpStatus.OK.value(),
            "Gudang berhasil diperbarui!",
            new Date(),
            updatedGudang
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}