package propensi.amesta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import propensi.amesta.model.Finance.Penerimaan;
import propensi.amesta.service.PenerimaanService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/penerimaan")
public class PenerimaanController {

    @Autowired
    private PenerimaanService penerimaanService;

    @PostMapping("/create")
    public ResponseEntity<Penerimaan> createPenerimaan(@RequestBody Penerimaan penerimaan) {
        Penerimaan savedPenerimaan = penerimaanService.createPenerimaan(penerimaan);
        return ResponseEntity.ok(savedPenerimaan);
    }

    @GetMapping("/viewall")
    public ResponseEntity<List<Penerimaan>> getAllPenerimaan() {
        return ResponseEntity.ok(penerimaanService.getAllPenerimaan());
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<Penerimaan> getPenerimaanById(@PathVariable UUID id) {
        Optional<Penerimaan> penerimaan = penerimaanService.getPenerimaanById(id);
        return penerimaan.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/view/{id}")
    public ResponseEntity<Penerimaan> updatePenerimaan(@PathVariable UUID id, @RequestBody Penerimaan newData) {
        try {
            Penerimaan updatedPenerimaan = penerimaanService.updatePenerimaan(id, newData);
            return ResponseEntity.ok(updatedPenerimaan);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/view/{id}")
    public ResponseEntity<Void> deletePenerimaan(@PathVariable UUID id) {
        try {
            penerimaanService.deletePenerimaan(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
