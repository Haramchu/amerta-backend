package propensi.amesta.service.Aset;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.Aset.Gudang;
import propensi.amesta.model.Aset.StockBarangPerGudang;
import propensi.amesta.model.Aset.TransferBarang;
import propensi.amesta.payload.request.BarangTransferDTO;
import propensi.amesta.payload.request.TransferBarangRequestDTO;
import propensi.amesta.payload.response.TransferBarangResponseDTO;
import propensi.amesta.repository.Aset.BarangDb;
import propensi.amesta.repository.Aset.GudangDb;
import propensi.amesta.repository.Aset.StockBarangPerGudangDb;
import propensi.amesta.repository.Aset.TransferBarangDb;

@Service
@Transactional
public class TransferBarangServiceImpl implements TransferBarangService {
    @Autowired
    private TransferBarangDb transferBarangDb;

    @Autowired
    private GudangDb gudangDb;

    @Autowired
    private BarangDb barangDb;

    @Autowired
    private StockBarangPerGudangDb stockBarangPerGudangDb;

    @Override
    public TransferBarangResponseDTO addTransferBarang(TransferBarangRequestDTO request) {

        Optional<Gudang> gudangAsalOpt = gudangDb.findById(request.getGudangAsal());
        if (gudangAsalOpt.isEmpty()) {
            throw new RuntimeException("Gudang asal tidak ditemukan: " + request.getGudangAsal());
        }
        Gudang gudangAsal = gudangAsalOpt.get();

        Optional<Gudang> gudangTujuanOpt = gudangDb.findById(request.getGudangTujuan());
        if (gudangTujuanOpt.isEmpty()) {
            throw new RuntimeException("Gudang tujuan tidak ditemukan: " + request.getGudangTujuan());
        }
        Gudang gudangTujuan = gudangTujuanOpt.get();

        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        int year = Integer.parseInt(yearFormat.format(request.getTanggalPemindahan()));
        int month = Integer.parseInt(monthFormat.format(request.getTanggalPemindahan()));
        int count = transferBarangDb.countByYearAndMonth(year, month) + 1;
        String sequence = String.format("%05d", count);
        String id = "IT." + year + "." + month + "." + sequence;

        List<Barang> listBarang = request.getListBarang().stream().map(barangTransferDTO -> {
            Optional<Barang> barangOpt = barangDb.findById(barangTransferDTO.getId());
            if (barangOpt.isEmpty()) {
                throw new RuntimeException("Barang tidak ditemukan: " + barangTransferDTO.getId());
            }
            Barang barang = barangOpt.get();
            
            StockBarangPerGudang stockAsal = stockBarangPerGudangDb.findByBarangAndGudang(barang, gudangAsal)
                    .orElseThrow(() -> new RuntimeException("Stok barang tidak ditemukan di gudang asal: " + barang.getNama()));

            if (stockAsal.getStock() < barangTransferDTO.getJumlah()) {
                throw new RuntimeException("Stok barang di gudang asal tidak mencukupi untuk pemindahan: " + barang.getNama());
            }

            stockAsal.setStock(stockAsal.getStock() - barangTransferDTO.getJumlah());
            stockBarangPerGudangDb.save(stockAsal);

            Optional<StockBarangPerGudang> stockTujuanOpt = stockBarangPerGudangDb.findByBarangAndGudang(barang, gudangTujuan);
            StockBarangPerGudang stockTujuan;

            if (stockTujuanOpt.isPresent()) {
                stockTujuan = stockTujuanOpt.get();
                stockTujuan.setStock(stockTujuan.getStock() + barangTransferDTO.getJumlah());
            } else {
                stockTujuan = new StockBarangPerGudang();
                stockTujuan.setBarang(barang);
                stockTujuan.setGudang(gudangTujuan);
                stockTujuan.setStock(barangTransferDTO.getJumlah());
            }

            stockBarangPerGudangDb.save(stockTujuan);

            return barang;
        }).collect(Collectors.toList());

        TransferBarang transferBarang = new TransferBarang();
        transferBarang.setId(id);
        transferBarang.setTanggalPemindahan(request.getTanggalPemindahan());
        transferBarang.setGudangAsal(gudangAsal);
        transferBarang.setGudangTujuan(gudangTujuan);
        transferBarang.setListBarang(listBarang);
        transferBarang.setCreatedDate(new Date());

        TransferBarang savedTransfer = transferBarangDb.save(transferBarang);

        return transferBarangToTransferBarangResponseDTO(savedTransfer, request.getListBarang());
    }

    private TransferBarangResponseDTO transferBarangToTransferBarangResponseDTO(TransferBarang transferBarang, List<BarangTransferDTO> listBarangDTO) {
        return new TransferBarangResponseDTO(
            transferBarang.getId(),
            transferBarang.getTanggalPemindahan(),
            transferBarang.getGudangAsal().getNama(),
            transferBarang.getGudangTujuan().getNama(),
            listBarangDTO,
            transferBarang.getCreatedDate()
        );
    }

    @Override
    public List<TransferBarangResponseDTO> getAllTransferBarang() {
        var listTransferBarang = transferBarangDb.findAll();
        var listTransferBarangResponseDTO = new ArrayList<TransferBarangResponseDTO>();
        listTransferBarang.forEach(transferBarang -> {
            var listBarangDTO = transferBarang.getListBarang().stream()
                .map(barang -> {
                    var barangDTO = new BarangTransferDTO();
                    barangDTO.setId(barang.getId());
                    var stockOpt = stockBarangPerGudangDb.findByBarangAndGudang(barang, transferBarang.getGudangTujuan());
                    if (stockOpt.isPresent()) {
                        barangDTO.setJumlah(stockOpt.get().getStock());
                    } else {
                        barangDTO.setJumlah(0);
                    }
                    return barangDTO;
                })
                .collect(Collectors.toList());
            var transferBarangResponseDTO = transferBarangToTransferBarangResponseDTO(transferBarang, listBarangDTO);
            listTransferBarangResponseDTO.add(transferBarangResponseDTO);
        });
        return listTransferBarangResponseDTO;
    }

    @Override
    public TransferBarangResponseDTO getTransferBarangByID(String id) {
        var transferBarang = transferBarangDb.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Transfer Barang with ID " + id + " not found"));
        var listBarangDTO = transferBarang.getListBarang().stream()
                .map(barang -> {
                    var barangDTO = new BarangTransferDTO();
                    barangDTO.setId(barang.getId());
                    var stockOpt = stockBarangPerGudangDb.findByBarangAndGudang(barang, transferBarang.getGudangTujuan());
                    if (stockOpt.isPresent()) {
                        barangDTO.setJumlah(stockOpt.get().getStock());
                    } else {
                        barangDTO.setJumlah(0);
                    }
                    return barangDTO;
                })
                .collect(Collectors.toList());
        return transferBarangToTransferBarangResponseDTO(transferBarang, listBarangDTO);
    }
}
