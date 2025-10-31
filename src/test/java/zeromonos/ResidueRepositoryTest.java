package zeromonos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import zeromonos.data.residues.Residue;
import zeromonos.data.residues.ResidueRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ResidueRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ResidueRepository residueRepository;

    @BeforeEach
    public void setup() {
        em.clear();
    }

    @Test
    public void whenFindNitroByName_thenReturnNitroResidue() {
        Residue r = em.persistFlushFind(new Residue("Nitroglycerin", 1f, 10f));

        Optional<Residue> persisted_r = residueRepository.findByName("Nitroglycerin");
        assertThat(persisted_r).isNotEmpty();
        assertThat(persisted_r.get()).isEqualTo(r);
    }

    @Test
    public void whenFindTritiumByName_thenReturnInvalidResidue() {
        Residue r = em.persistFlushFind(new Residue("Nitroglycerin", 1f, 10f));

        Optional<Residue> persisted_r = residueRepository.findByName("Tritium");
        assertThat(persisted_r).isEmpty();
    }

    @Test
    public void whenFindByValidID_thenReturnValidResidue() {
        Residue r = em.persistFlushFind(new Residue("Nitroglycerin", 1f, 10f));

        Optional<Residue> persisted_r = residueRepository.findById(r.getId());
        assertThat(persisted_r).isNotEmpty();
        assertThat(persisted_r.get()).isEqualTo(r);
    }

    @Test
    public void whenFindByInvalidID_thenReturnInvalidResidue() {
        Residue r = em.persistFlushFind(new Residue("Nitroglycerin", 1f, 10f));

        Optional<Residue> persisted_r = residueRepository.findById(99999L);
        assertThat(persisted_r).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Nitro", "glycerin", "BOOM"})
    public void whenFindNitroBySimilarNameOrDesc_thenReturnNitroResidue(String search_str) {
        Residue r1 = em.persistFlushFind(new Residue("Nitroglycerin", 1f, 10f));
        Residue r2 = em.persistFlushFind(new Residue("TNT", "Makes everything go boom", 2f, 5f));

        List<Residue> residues = residueRepository.findAllBySimilarNameOrDesc(search_str);
        assertThat(residues).isNotEmpty().containsAnyOf(r1, r2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"dogs", "cats", "cars"})
    public void whenFindInvalidBySimilarNameOrDesc_thenReturnInvalidResidue(String search_str) {
        Residue r1 = em.persistFlushFind(new Residue("Nitroglycerin", 1f, 10f));
        Residue r2 = em.persistFlushFind(new Residue("TNT", "Makes everything go boom", 5f, 3f));

        List<Residue> residues = residueRepository.findAllBySimilarNameOrDesc(search_str);
        assertThat(residues).doesNotContain(r1, r2).isEmpty();
    }
}
