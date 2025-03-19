package propensi.amesta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import propensi.amesta.model.Finance.Pengeluaran;
import propensi.amesta.service.PengeluaranService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/pengeluaran")
public class PengeluaranController {

    @Autowired
    private PengeluaranService pengeluaranService;

    @PostMapping
    public ResponseEntity<Pengeluaran> createPengeluaran(@RequestBody Pengeluaran pengeluaran) {
        Pengeluaran savedPengeluaran = pengeluaranService.createPengeluaran(pengeluaran);
        return ResponseEntity.ok(savedPengeluaran);
    }

    @GetMapping
    public ResponseEntity<List<Pengeluaran>> getAllPengeluaran() {
        return ResponseEntity.ok(pengeluaranService.getAllPengeluaran());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pengeluaran> getPengeluaranById(@PathVariable UUID id) {
        Optional<Pengeluaran> pengeluaran = pengeluaranService.getPengeluaranById(id);
        return pengeluaran.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pengeluaran> updatePengeluaran(@PathVariable UUID id, @RequestBody Pengeluaran newData) {
        try {
            Pengeluaran updatedPengeluaran = pengeluaranService.updatePengeluaran(id, newData);
            return ResponseEntity.ok(updatedPengeluaran);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePengeluaran(@PathVariable UUID id) {
        try {
            pengeluaranService.deletePengeluaran(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
