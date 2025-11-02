package zeromonos.services.residues;

import zeromonos.data.residues.Residue;

import java.util.List;
import java.util.Optional;

public interface ResidueServiceInterface {

    Long createResidue(Residue residue);

    Optional<Residue> getResidue(Long id);

    void deleteResidue(Long id);

    List<Residue> getResiduesBySimilarNameOrDesc(String query);

    List<Residue> getAllResidues();
}
