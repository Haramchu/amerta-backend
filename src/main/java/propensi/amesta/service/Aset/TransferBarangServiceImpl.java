package propensi.amesta.service.Aset;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.Aset.Gudang;
import propensi.amesta.model.Aset.KuantitasBarangPerTransfer;
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

        // Validasi Gudang Asal
        Gudang gudangAsal = gudangDb.findById(request.getGudangAsal())
            .orElseThrow(() -> new RuntimeException("Gudang asal tidak ditemukan: " + request.getGudangAsal()));

        // Validasi Gudang Tujuan
        Gudang gudangTujuan = gudangDb.findById(request.getGudangTujuan())
            .orElseThrow(() -> new RuntimeException("Gudang tujuan tidak ditemukan: " + request.getGudangTujuan()));

        // Generate ID berdasarkan Tahun dan Bulan
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        int year = Integer.parseInt(yearFormat.format(request.getTanggalPemindahan()));
        int month = Integer.parseInt(monthFormat.format(request.getTanggalPemindahan()));
        int count = transferBarangDb.countByYearAndMonth(year, month) + 1;
        String sequence = String.format("%05d", count);
        String id = "IT." + year + "." + month + "." + sequence;

        // Simpan TransferBarang
        TransferBarang transferBarang = new TransferBarang();
        transferBarang.setId(id);
        transferBarang.setTanggalPemindahan(request.getTanggalPemindahan());
        transferBarang.setGudangAsal(gudangAsal);
        transferBarang.setGudangTujuan(gudangTujuan);
        transferBarang.setCreatedDate(new Date());

        List<KuantitasBarangPerTransfer> listKuantitasBarang = new ArrayList<>();

        for (BarangTransferDTO barangDTO : request.getListBarang()) {
            Barang barang = barangDb.findById(barangDTO.getId())
                .orElseThrow(() -> new RuntimeException("Barang tidak ditemukan: " + barangDTO.getId()));

            // Kurangi stok dari Gudang Asal
            StockBarangPerGudang stockAsal = stockBarangPerGudangDb.findByBarangAndGudang(barang, gudangAsal)
                .orElseThrow(() -> new RuntimeException("Stok barang tidak ditemukan di gudang asal: " + barang.getNama()));

            if (stockAsal.getStock() < barangDTO.getJumlah()) {
                throw new RuntimeException("Stok barang di gudang asal tidak mencukupi untuk pemindahan: " + barang.getNama());
            }
            stockAsal.setStock(stockAsal.getStock() - barangDTO.getJumlah());
            stockBarangPerGudangDb.save(stockAsal);

            // Tambahkan stok ke Gudang Tujuan
            StockBarangPerGudang stockTujuan = stockBarangPerGudangDb.findByBarangAndGudang(barang, gudangTujuan)
                .orElseGet(() -> new StockBarangPerGudang());
            stockTujuan.setBarang(barang);
            stockTujuan.setGudang(gudangTujuan);
            stockTujuan.setStock(stockTujuan.getStock() + barangDTO.getJumlah());
            stockBarangPerGudangDb.save(stockTujuan);

            // Simpan kuantitas barang per transfer
            KuantitasBarangPerTransfer kuantitasBarang = new KuantitasBarangPerTransfer();
            kuantitasBarang.setTransferBarang(transferBarang);
            kuantitasBarang.setBarang(barang);
            kuantitasBarang.setJumlah(barangDTO.getJumlah());

            listKuantitasBarang.add(kuantitasBarang);
        }

        transferBarang.setKuantitasBarang(listKuantitasBarang);
        TransferBarang savedTransfer = transferBarangDb.save(transferBarang);

        return transferBarangToTransferBarangResponseDTO(savedTransfer);
    }

    private TransferBarangResponseDTO transferBarangToTransferBarangResponseDTO(TransferBarang transferBarang) {
        List<BarangTransferDTO> listBarangDTO = transferBarang.getKuantitasBarang().stream()
            .map(kbt -> new BarangTransferDTO(kbt.getBarang().getId(), kbt.getJumlah()))
            .collect(Collectors.toList());

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
        return transferBarangDb.findAll().stream()
            .map(this::transferBarangToTransferBarangResponseDTO)
            .collect(Collectors.toList());
    }

    @Override
    public TransferBarangResponseDTO getTransferBarangByID(String id) {
        TransferBarang transferBarang = transferBarangDb.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Transfer Barang dengan ID " + id + " tidak ditemukan"));
        return transferBarangToTransferBarangResponseDTO(transferBarang);
    }

}
