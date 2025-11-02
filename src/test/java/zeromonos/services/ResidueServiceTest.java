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
public class ResidueServiceTest {

    @Mock
    private ResidueRepository residueRepository;

    @InjectMocks
    private ResidueService residueService;

    private Residue plastic;
    private Residue wood;

    @BeforeEach
    public void setup() {
        plastic = new Residue("Plastic", "My plastic", 1f, 2f);
        plastic.setId(0L);

        wood = new Residue("Wood", "My wood", 1f, 2f);
        wood.setId(1L);
        List<Residue> residues = Arrays.asList(plastic, wood);

        when(residueRepository.save(plastic)).thenReturn(plastic);

        when(residueRepository.findById(plastic.getId()))
                .thenReturn(Optional.of(plastic));

        when(residueRepository.findById(-1L))
                .thenReturn(Optional.empty());

        when(residueRepository.findAllBySimilarNameOrDesc("My"))
                .thenReturn(residues);
        when(residueRepository.findAllBySimilarNameOrDesc(null))
                .thenReturn(List.of());
    }

    @Test
    public void testCreateResidue_Success() {
        Long id = residueService.createResidue(plastic);

        assertThat(id).isEqualTo(plastic.getId());
        verify(residueRepository, times(1)).save(plastic);
    }

    @Test
    public void testGetResidue_ExistingId() {
        Optional<Residue> result = residueService.getResidue(plastic.getId());

        assertThat(result).isNotEmpty();
        assertThat(result.get().getId()).isEqualTo(plastic.getId());
        verify(residueRepository, times(1)).findById(plastic.getId());
    }

    @Test
    public void testGetResidue_NotFound() {
        Optional<Residue> residueOptional =  residueService.getResidue(-1L);

        assertThat(residueOptional).isEmpty();
        verify(residueRepository, times(1)).findById(-1L);
    }

    @Test
    public void testDeleteResidue_Success() {
        residueService.deleteResidue(plastic.getId());

        verify(residueRepository, times(1)).findById(plastic.getId());
        verify(residueRepository, times(1)).delete(plastic);
    }

    @Test
    public void testDeleteResidue_NotFound() {

        assertThatThrownBy(() -> residueService.deleteResidue(-1L)).isInstanceOf(NoSuchElementException.class);
        verify(residueRepository, times(1)).findById(-1L);
        verify(residueRepository, never()).delete(any());
    }

    @Test
    public void testDeleteResidue_AlreadyBelongsToRequest() {
        Residue linkedResidue = new Residue("Linked", "Has request", 1f, 1f);
        linkedResidue.setId(3L);
        linkedResidue.setRequest(new Request());

        when(residueRepository.findById(linkedResidue.getId()))
                .thenReturn(Optional.of(linkedResidue));

        assertThatThrownBy(() -> residueService.deleteResidue(linkedResidue.getId()));
        verify(residueRepository, times(1)).findById(linkedResidue.getId());
        verify(residueRepository, never()).delete(linkedResidue);
    }

    @Test
    public void testGetResiduesBySimilarNameOrDesc_ValidQuery() {
        List<Residue> result = residueService.getResiduesBySimilarNameOrDesc("My");

        assertThat(result).hasSize(2).contains(plastic, wood);
        verify(residueRepository, times(1)).findAllBySimilarNameOrDesc("My");
    }

    @Test
    public void testGetResiduesBySimilarNameOrDesc_NullQuery() {
        List<Residue> result = residueService.getResiduesBySimilarNameOrDesc(null);

        assertThat(result).isEmpty();
        verify(residueRepository, times(1)).findAllBySimilarNameOrDesc(null);
    }
}
