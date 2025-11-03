package zeromonos.boundary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import zeromonos.data.residues.Residue;
import zeromonos.data.residues.ResidueDTO;
import zeromonos.services.residues.ResidueService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/residues")
public class ResidueController {

    private final ResidueService residueService;

    public ResidueController(ResidueService residueService) {
        this.residueService = residueService;
    }

    @GetMapping
    public ResponseEntity<List<ResidueDTO>> getAllResidues() {
        List<Residue> residues = residueService.getAllResidues();
        return ResponseEntity.ok(
                residues.stream().map(ResidueDTO::fromResidueEntity).toList()
        );
    }

    @PostMapping
    public ResponseEntity<Long> createResidue(@RequestBody ResidueDTO residue) {
        Long id = residueService.createResidue(ResidueDTO.toResidueEntity(residue));
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Residue> getResidue(@PathVariable("id") Long id) {
        Optional<Residue> residueOptional = residueService.getResidue(id);
        if (residueOptional.isPresent()) {
            return ResponseEntity.ok(residueOptional.get());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Residue not found");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResidue(@PathVariable("id") Long id) {
        try {
            residueService.deleteResidue(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/search/{query}")
    public ResponseEntity<List<ResidueDTO>> searchResidues(@PathVariable("query") String query) {
        List<Residue> results = residueService.getResiduesBySimilarNameOrDesc(query);
        return ResponseEntity.ok(
                results.stream().map(ResidueDTO::fromResidueEntity).toList()
        );
    }
}
