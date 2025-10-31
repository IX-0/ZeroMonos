package zeromonos.services;

import zeromonos.data.Residue;

public interface ResidueServiceInterface {

    public Long createResidue();

    public Residue getResidue(Long id);

    public void deleteResidue(Long id);
}
