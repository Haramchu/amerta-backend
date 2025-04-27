package propensi.amesta.service.Aset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.Aset.Gudang;
import propensi.amesta.model.Aset.StockBarangPerGudang;
import propensi.amesta.payload.response.NamaGudangPerBarangResponseDTO;
import propensi.amesta.payload.request.Aset.BarangRequestDTO;
import propensi.amesta.payload.request.Aset.StockBarangRequestDTO;
import propensi.amesta.payload.request.Aset.UpdateBarangRequestDTO;
import propensi.amesta.payload.response.Aset.BarangResponseDTO;
import propensi.amesta.payload.response.Aset.StockBarangResponseDTO;
import propensi.amesta.repository.Aset.BarangDb;
import propensi.amesta.repository.Aset.GudangDb;

@Service
public class BarangServiceImpl implements BarangService {

    @Autowired
    private BarangDb barangDb;

    @Autowired
    private GudangDb gudangDb;

    @Override
    public BarangResponseDTO addBarang(BarangRequestDTO barangRequestDTO) {
        for (StockBarangRequestDTO stockBarangRequestDTO : barangRequestDTO.getListStockBarang()) {
            gudangDb.findById(stockBarangRequestDTO.getNamaGudang())
                .orElseThrow(() -> new RuntimeException("Gudang dengan ID '" + stockBarangRequestDTO.getNamaGudang() + "' tidak tersedia"));
        }

        if(barangDb.findAll().size() > 0){
            if (barangDb.existsByNamaAndMerk(barangRequestDTO.getNama(), barangRequestDTO.getMerk())) {
                throw new RuntimeException("Barang dengan nama dan merk yang sama sudah ada. Jika ingin menambahkan stok, gunakan fitur update barang.");
            }    
        }

        Barang barang = new Barang();
        barang.setNama(barangRequestDTO.getNama().strip());
        barang.setKategori(barangRequestDTO.getKategori().strip());
        barang.setMerk(barangRequestDTO.getMerk().strip());
        barang.setActive(barangRequestDTO.isActive());
        barang.setHargaBeli(barangRequestDTO.getHargaBeli());
        barang.setHargaJual(barangRequestDTO.getHargaJual());
        barang.setId(generateId());

        List<StockBarangPerGudang> listStockBarang = new ArrayList<>();
        Map<Gudang, Integer> gudangStockMap = new HashMap<>();

        for (StockBarangRequestDTO stockBarangRequestDTO : barangRequestDTO.getListStockBarang()) {
            Gudang gudang = gudangDb.findById(stockBarangRequestDTO.getNamaGudang())
                .orElseThrow(() -> new RuntimeException("Gudang dengan ID '" + stockBarangRequestDTO.getNamaGudang() + "' tidak tersedia"));

            gudangStockMap.put(gudang, gudangStockMap.getOrDefault(gudang, 0) + stockBarangRequestDTO.getStock());
        }

        for (Map.Entry<Gudang, Integer> entry : gudangStockMap.entrySet()) {
            if (entry.getValue() < 0) {
                throw new IllegalArgumentException("Total stok barang untuk gudang '" + entry.getKey().getNama() + "' harus lebih besar atau sama dengan 0");
            }

            StockBarangPerGudang stockBarangPerGudang = new StockBarangPerGudang();
            stockBarangPerGudang.setGudang(entry.getKey());
            stockBarangPerGudang.setStock(entry.getValue());
            stockBarangPerGudang.setBarang(barang);
            listStockBarang.add(stockBarangPerGudang);
        }
        
        for (Map.Entry<Gudang, Integer> entry : gudangStockMap.entrySet()) {
            Gudang gudang = entry.getKey();
            int totalStock = entry.getValue() + (gudang.getListBarang() != null
                ? gudang.getListBarang().stream().mapToInt(stock -> stock.getStock()).sum()
                : 0);
        
            if (gudang.getKapasitas() < totalStock) {
                throw new RuntimeException("Kapasitas gudang '" + gudang.getNama() + "' tidak mencukupi");
            }
        }


        barang.setListStockBarang(listStockBarang);
       

        return barangToBarangResponseDTO(barangDb.save(barang));
    }


    public String generateId() {
        // Format ID: yyyy-MM-dd-XXXXXXX, MM dalam romawi, XXXXXXX adalah random string
        String id = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .replace("-01-", "-I-")
            .replace("-02-", "-II-")
            .replace("-03-", "-III-")
            .replace("-04-", "-IV-")
            .replace("-05-", "-V-")
            .replace("-06-", "-VI-")
            .replace("-07-", "-VII-")
            .replace("-08-", "-VIII-")
            .replace("-09-", "-IX-")
            .replace("-10-", "-X-")
            .replace("-11-", "-XI-")
            .replace("-12-", "-XII-");
    
        id += "-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return id;
    }    

    @Override
    public BarangResponseDTO getBarangById(String id) {
        Barang barang = barangDb.findById(id)
            .orElseThrow(() -> new RuntimeException("Barang dengan ID '" + id + "' tidak ditemukan"));

        return barangToBarangResponseDTO(barang);
    }

    @Override
    public BarangResponseDTO updateBarang(String id, UpdateBarangRequestDTO barangRequestDTO) {
        Barang barang = barangDb.findById(id)
            .orElseThrow(() -> new RuntimeException("Barang dengan ID '" + id + " tidak ditemukan"));
    
        for (StockBarangRequestDTO stockBarangRequestDTO : barangRequestDTO.getListStockBarang()) {
            gudangDb.findById(stockBarangRequestDTO.getNamaGudang())
                .orElseThrow(() -> new RuntimeException("Gudang dengan ID '" + stockBarangRequestDTO.getNamaGudang() + "' tidak tersedia"));
        }
    
        List<StockBarangPerGudang> existingList = barang.getListStockBarang();

        Map<String, Integer> mergedStockMap = new HashMap<>();
        for (StockBarangRequestDTO stockBarangRequestDTO : barangRequestDTO.getListStockBarang()) {
            mergedStockMap.put(
                stockBarangRequestDTO.getNamaGudang(),
                mergedStockMap.getOrDefault(stockBarangRequestDTO.getNamaGudang(), 0) + stockBarangRequestDTO.getStock()
            );
        }
    
        Map<String, StockBarangPerGudang> existingStockMap = new HashMap<>();
        for (StockBarangPerGudang stock : existingList) {
            existingStockMap.put(stock.getGudang().getNama(), stock);
        }
    
        for (Map.Entry<String, Integer> entry : mergedStockMap.entrySet()) {
            String namaGudang = entry.getKey();
            int totalStock = entry.getValue();
    
            Gudang gudang = gudangDb.findById(namaGudang)
                .orElseThrow(() -> new RuntimeException("Gudang dengan ID '" + namaGudang + "' tidak tersedia"));
    
            if (existingStockMap.containsKey(namaGudang)) {
                StockBarangPerGudang existingStock = existingStockMap.get(namaGudang);
                existingStock.setStock(totalStock);
            } else {
                StockBarangPerGudang newStock = new StockBarangPerGudang();
                newStock.setGudang(gudang);
                newStock.setStock(totalStock);
                newStock.setBarang(barang);
                existingList.add(newStock);
            }
        }
    
        existingList.removeIf(stock -> !mergedStockMap.containsKey(stock.getGudang().getNama()));
    
        for (StockBarangPerGudang stock : existingList) {
            Gudang gudang = stock.getGudang();
            int totalStock = stock.getStock() + (gudang.getListBarang() != null
                ? gudang.getListBarang().stream().mapToInt(s -> s.getStock()).sum()
                : 0);
    
            if (gudang.getKapasitas() < totalStock) {
                throw new RuntimeException("Kapasitas gudang '" + gudang.getNama() + "' tidak mencukupi. Tidak ada perubahan yang dilakukan.");
            }
        }

        Barang existingBarang = barangDb.findByNamaAndMerk(barangRequestDTO.getNama(), barangRequestDTO.getMerk()).size() > 0
            ? barangDb.findByNamaAndMerk(barangRequestDTO.getNama(), barangRequestDTO.getMerk()).get(0)
            : null;
        if (existingBarang != null && !existingBarang.getId().equals(id)) {
            throw new RuntimeException("Barang dengan nama dan merk yang sama sudah ada.");
        }

        barang.setNama(barangRequestDTO.getNama().strip());
        barang.setKategori(barangRequestDTO.getKategori().strip());
        barang.setMerk(barangRequestDTO.getMerk().strip());
        barang.setHargaBeli(barangRequestDTO.getHargaBeli());
        barang.setHargaJual(barangRequestDTO.getHargaJual());
        barang.setActive(barangRequestDTO.isActive());
    
        return barangToBarangResponseDTO(barangDb.save(barang));
    }
    
    
    @Override
    public List<BarangResponseDTO> getBarangByKategori(String kategori) {
        List<BarangResponseDTO> barangResponseDTOList = new ArrayList<>();
        List<Barang> barangList = barangDb.findByKategori(kategori);
        if (barangList.isEmpty()) {
            throw new RuntimeException("Barang dengan kategori '" + kategori + "' tidak ditemukan");
        }

        for (Barang barang : barangList) {
            barangResponseDTOList.add(barangToBarangResponseDTO(barang));
        }
        
        return barangResponseDTOList;
    }

    @Override
    public List<BarangResponseDTO> getBarangByMerk(String merk) {
        List<BarangResponseDTO> barangResponseDTOList = new ArrayList<>();
        List<Barang> barangList = barangDb.findByMerk(merk);
        if (barangList.isEmpty()) {
            throw new RuntimeException("Barang dengan merk '" + merk + "' tidak ditemukan");
        }

        for (Barang barang : barangList) {
            barangResponseDTOList.add(barangToBarangResponseDTO(barang));
        }

        return barangResponseDTOList;
    }

    public BarangResponseDTO barangToBarangResponseDTO(Barang barang){
        BarangResponseDTO barangResponseDTO = new BarangResponseDTO();
        List<StockBarangResponseDTO> stockBarangResponseDTO = stockBarangToStockBarangResponseDTO(barang.getListStockBarang());
        barangResponseDTO.setId(barang.getId());
        barangResponseDTO.setNama(barang.getNama());
        barangResponseDTO.setKategori(barang.getKategori());
        barangResponseDTO.setActive(barang.isActive());
        barangResponseDTO.setMerk(barang.getMerk());
        barangResponseDTO.setStockBarang(stockBarangResponseDTO);
        barangResponseDTO.setTotalStock(stockBarangResponseDTO.stream().mapToInt(stock -> stock.getStock()).sum());
        barangResponseDTO.setHargaBeli(barang.getHargaBeli());
        barangResponseDTO.setHargaJual(barang.getHargaJual());
        barangResponseDTO.setCreatedDate(barang.getCreatedDate());
        barangResponseDTO.setUpdatedDate(barang.getUpdatedDate());
        return barangResponseDTO;
    }

    public List<StockBarangResponseDTO> stockBarangToStockBarangResponseDTO(List<StockBarangPerGudang> stockBarang){
        List<StockBarangResponseDTO> stockBarangResponseDTO = new ArrayList<>();
        for (StockBarangPerGudang stock : stockBarang) {
            StockBarangResponseDTO stockBarangResponse = new StockBarangResponseDTO();
            stockBarangResponse.setIdBarang(stock.getBarang().getId());
            stockBarangResponse.setNamaGudang(stock.getGudang().getNama());
            stockBarangResponse.setStock(stock.getStock());
            stockBarangResponseDTO.add(stockBarangResponse);
        }
        return stockBarangResponseDTO;
    }


    @Override
    public List<BarangResponseDTO> getAllBarang() {
        List<BarangResponseDTO> barangResponseDTOList = new ArrayList<>();
        List<Barang> barangList = barangDb.findAll();
        if (barangList.isEmpty()) {
            throw new RuntimeException("Tidak ada data barang yang tersedia");
        }

        for (Barang barang : barangList) {
            barangResponseDTOList.add(barangToBarangResponseDTO(barang));
        }

        return barangResponseDTOList;
    }

    @Override
    public BarangResponseDTO changeStatusBarang(String id) {
        Barang barang = barangDb.findById(id)
            .orElseThrow(() -> new RuntimeException("Barang dengan ID '" + id + "' tidak ditemukan"));

        barang.setActive(!barang.isActive());
        return barangToBarangResponseDTO(barangDb.save(barang));
    }

    @Override
    public NamaGudangPerBarangResponseDTO getAllNamaGudangPerBarang(String id) {
        Barang barang = barangDb.findById(id)
            .orElseThrow(() -> new RuntimeException("Barang dengan ID '" + id + "' tidak ditemukan"));

        List<String> namaGudangList = new ArrayList<>();
        NamaGudangPerBarangResponseDTO namaGudang = new NamaGudangPerBarangResponseDTO();

        for (StockBarangPerGudang stock : barang.getListStockBarang()) {
            namaGudangList.add(stock.getGudang().getNama());
        }

        namaGudang.setNamaGudang(namaGudangList);

        return namaGudang;
    }    
}
