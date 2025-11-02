package zeromonos.services.residues;

import org.springframework.stereotype.Service;
import zeromonos.data.residues.Residue;
import zeromonos.data.residues.ResidueRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ResidueService implements ResidueServiceInterface {

    private final ResidueRepository residueRepository;

    public ResidueService(ResidueRepository residueRepository) {
        this.residueRepository = residueRepository;
    }

    @Override
    public Long createResidue(Residue residue) {
        return residueRepository.save(residue).getId();
    }

    @Override
    public Optional<Residue> getResidue(Long id) {
        return residueRepository.findById(id);
    }

    @Override
    public void deleteResidue(Long id){
        Optional<Residue> residueOptional = residueRepository.findById(id);

        if (residueOptional.isPresent()) {
            Residue residue = residueOptional.get();

            if (residue.getRequest() == null) {
                residueRepository.delete(residue);
            } else {
                throw new NoSuchElementException("Residue already belongs to a Request ");
            }

        } else  {
            throw new NoSuchElementException("Residue with id " + id + " not found");
        }
    }

    @Override
    public List<Residue> getResiduesBySimilarNameOrDesc(String query) {
        return residueRepository.findAllBySimilarNameOrDesc(query);
    }

    @Override
    public List<Residue> getAllResidues() {
        return residueRepository.findAll();
    }
}
