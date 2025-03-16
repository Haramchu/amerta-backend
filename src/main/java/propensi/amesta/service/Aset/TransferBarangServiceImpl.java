package propensi.amesta.service.Aset;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import propensi.amesta.model.Aset.Barang;
import propensi.amesta.model.Aset.Gudang;
import propensi.amesta.model.Aset.TransferBarang;
import propensi.amesta.payload.request.TransferBarangRequestDTO;
import propensi.amesta.payload.response.TransferBarangResponseDTO;
import propensi.amesta.repository.Aset.BarangDb;
import propensi.amesta.repository.Aset.GudangDb;
import propensi.amesta.repository.Aset.TransferBarangDb;

@Service
public class TransferBarangServiceImpl implements TransferBarangService {
    @Autowired
    private TransferBarangDb transferBarangDb;

    @Autowired
    private GudangDb gudangDb;

    @Autowired
    private BarangDb barangDb;

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

        List<Barang> listBarang = request.getListBarang().stream()
            .map(id -> barangDb.findById(id)
                .orElseThrow(() -> new RuntimeException("Barang tidak ditemukan: " + id)))
            .collect(Collectors.toList());

        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");

        int year = Integer.parseInt(yearFormat.format(request.getTanggalPemindahan()));
        int month = Integer.parseInt(monthFormat.format(request.getTanggalPemindahan()));

        int count = transferBarangDb.countByYearAndMonth(year, month) + 1;
        String sequence = String.format("%05d", count);
        String id = "IT." + year + "." + month + "." + sequence;

        TransferBarang transferBarang = new TransferBarang();
        transferBarang.setId(id);
        transferBarang.setTanggalPemindahan(request.getTanggalPemindahan());
        transferBarang.setGudangAsal(gudangAsal);
        transferBarang.setGudangTujuan(gudangTujuan);
        transferBarang.setListBarang(listBarang);
        transferBarang.setCreatedDate(new Date());

        TransferBarang savedTransfer = transferBarangDb.save(transferBarang);

         return new TransferBarangResponseDTO(
            savedTransfer.getId(),
            savedTransfer.getTanggalPemindahan(),
            savedTransfer.getGudangAsal().getNama(),
            savedTransfer.getGudangTujuan().getNama(),
            savedTransfer.getListBarang().stream().map(Barang::getNama).collect(Collectors.toList()),
            savedTransfer.getCreatedDate()
        );
    }
}