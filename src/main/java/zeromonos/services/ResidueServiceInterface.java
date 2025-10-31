package zeromonos.services;

import zeromonos.data.residues.Residue;

public interface ResidueServiceInterface {

    public Long createResidue();

    public Residue getResidue(Long id);

    public void deleteResidue(Long id);
}
