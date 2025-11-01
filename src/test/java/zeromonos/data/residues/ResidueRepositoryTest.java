package zeromonos.data.residues;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ResidueRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ResidueRepository residueRepository;

    @BeforeEach
    void setup() {
        em.clear();
    }

    @Test
    void whenFindNitroByName_thenReturnNitroResidue() {
        Residue r0 = em.persistFlushFind(new Residue("Nitroglycerin", 1f, 10f));

        Optional<Residue> persistedResidue = residueRepository.findByName("Nitroglycerin");
        assertThat(persistedResidue).isNotEmpty().contains(r0);
    }

    @Test
    void whenFindTritiumByName_thenReturnInvalidResidue() {
        em.persistFlushFind(new Residue("Nitroglycerin", 1f, 10f));

        Optional<Residue> persistedResidue = residueRepository.findByName("Tritium");
        assertThat(persistedResidue).isEmpty();
    }

    @Test
    void whenFindByValidID_thenReturnValidResidue() {
        Residue r0 = em.persistFlushFind(new Residue("Nitroglycerin", 1f, 10f));

        Optional<Residue> persistedResidue = residueRepository.findById(r0.getId());
        assertThat(persistedResidue).isNotEmpty().contains(r0);
    }

    @Test
    void whenFindByInvalidID_thenReturnInvalidResidue() {
        em.persistFlushFind(new Residue("Nitroglycerin", 1f, 10f));

        Optional<Residue> persistedResidue = residueRepository.findById(99999L);
        assertThat(persistedResidue).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Nitro", "glycerin", "BOOM"})
    void whenFindNitroBySimilarNameOrDesc_thenReturnNitroResidue(String search_str) {
        Residue r1 = em.persistFlushFind(new Residue("Nitroglycerin", 1f, 10f));
        Residue r2 = em.persistFlushFind(new Residue("TNT", "Makes everything go boom", 2f, 5f));

        List<Residue> residues = residueRepository.findAllBySimilarNameOrDesc(search_str);
        assertThat(residues).isNotEmpty().containsAnyOf(r1, r2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"dogs", "cats", "cars"})
    void whenFindInvalidBySimilarNameOrDesc_thenReturnInvalidResidue(String search_str) {
        Residue r1 = em.persistFlushFind(new Residue("Nitroglycerin", 1f, 10f));
        Residue r2 = em.persistFlushFind(new Residue("TNT", "Makes everything go boom", 5f, 3f));

        List<Residue> residues = residueRepository.findAllBySimilarNameOrDesc(search_str);
        assertThat(residues).doesNotContain(r1, r2).isEmpty();
    }
}
