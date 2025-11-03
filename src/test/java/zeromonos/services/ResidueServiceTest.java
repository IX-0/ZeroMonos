package zeromonos.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import zeromonos.data.requests.Request;
import zeromonos.data.residues.Residue;
import zeromonos.data.residues.ResidueRepository;
import zeromonos.services.residues.ResidueService;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ResidueServiceTest {

    @Mock
    private ResidueRepository residueRepository;

    @InjectMocks
    private ResidueService residueService;

    private Residue plastic;
    private Residue wood;
    private Residue linkedResidue;

    @BeforeEach
    void setup() {
        plastic = new Residue("Plastic", "My plastic", 1f, 2f);
        plastic.setId(0L);

        wood = new Residue("Wood", "My wood", 1f, 2f);
        wood.setId(1L);
        List<Residue> residues = Arrays.asList(plastic, wood);

        linkedResidue = new Residue("Linked", "Has request", 1f, 1f);
        linkedResidue.setId(3L);
        linkedResidue.setRequest(new Request());

        when(residueRepository.findById(linkedResidue.getId()))
                .thenReturn(Optional.of(linkedResidue));

        when(residueRepository.save(plastic)).thenReturn(plastic);

        when(residueRepository.findById(plastic.getId()))
                .thenReturn(Optional.of(plastic));

        when(residueRepository.findById(-1L))
                .thenReturn(Optional.empty());

        when(residueRepository.findAll())
                .thenReturn(residues);

        when(residueRepository.findAllBySimilarNameOrDesc("Wood"))
                .thenReturn(List.of(wood));
        when(residueRepository.findAllBySimilarNameOrDesc(null))
                .thenReturn(List.of());
    }

    @Test
    void createResidue_shouldReturnId() {
        Long id = residueService.createResidue(plastic);

        assertThat(id).isEqualTo(plastic.getId());
        verify(residueRepository, times(1)).save(plastic);
    }

    @Test
    void getResidue_shouldReturnResidue() {
        Optional<Residue> result = residueService.getResidue(plastic.getId());

        assertThat(result).isNotEmpty();
        assertThat(result.get().getId()).isEqualTo(plastic.getId());
        verify(residueRepository, times(1)).findById(plastic.getId());
    }

    @Test
    void getResidue_shouldReturnNothing() {
        Optional<Residue> residueOptional =  residueService.getResidue(-1L);

        assertThat(residueOptional).isEmpty();
        verify(residueRepository, times(1)).findById(-1L);
    }

    @Test
    void deleteResidue_shouldResultInSuccess() {
        residueService.deleteResidue(plastic.getId());

        verify(residueRepository, times(1)).findById(plastic.getId());
        verify(residueRepository, times(1)).delete(plastic);
    }

    @Test
    void deleteResidue_shouldThrowWhenResidueNotFound() {
        assertThatThrownBy(() -> residueService.deleteResidue(-1L)).isInstanceOf(NoSuchElementException.class);
        verify(residueRepository, times(1)).findById(-1L);
        verify(residueRepository, never()).delete(any());
    }

    @Test
    void deleteResidue_shouldThrowWhenResidueBelongsToRequest() {

        assertThatThrownBy(() -> residueService.deleteResidue(linkedResidue.getId()));
        verify(residueRepository, times(1)).findById(linkedResidue.getId());
        verify(residueRepository, never()).delete(linkedResidue);
    }

    @Test
    void getResiduesBySimilarNameOrDesc_ValidQuery() {
        List<Residue> result = residueService.getResiduesBySimilarNameOrDesc("Wood");

        assertThat(result).hasSize(1).contains(wood);
        verify(residueRepository, times(1)).findAllBySimilarNameOrDesc("Wood");
    }

    @Test
    void getResiduesBySimilarNameOrDesc_NullQuery() {
        List<Residue> result = residueService.getResiduesBySimilarNameOrDesc(null);

        assertThat(result).isEmpty();
        verify(residueRepository, times(1)).findAllBySimilarNameOrDesc(null);
    }

    @Test
    void getAllResidues() {
        List<Residue> result = residueService.getAllResidues();

        assertThat(result).hasSize(2).contains(plastic, wood);
        verify(residueRepository, times(1)).findAll();
    }

}
