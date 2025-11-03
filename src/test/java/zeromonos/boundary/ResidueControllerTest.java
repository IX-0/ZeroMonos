package zeromonos.boundary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import zeromonos.data.residues.Residue;
import zeromonos.services.residues.ResidueService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(controllers = ResidueController.class)
class ResidueControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ResidueService residueService;

    @Test
    void getAllResidues_shouldReturnList() throws Exception {
        Residue residue = new Residue("Plastic", "A ton of microplastics", 1f, 1f);
        residue.setId(0L);

        when(residueService.getAllResidues()).thenReturn(List.of(residue));

        mvc.perform(get("/api/residues"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$[0].id", is(0)))
                    .andExpect(jsonPath("$[0].name", is("Plastic")))
                    .andExpect(jsonPath("$[0].desc", is("A ton of microplastics")));

        verify(residueService, times(1)).getAllResidues();
    }

    @Test
    void getAllResidues_shouldReturnEmptyList() throws Exception {
        when(residueService.getAllResidues()).thenReturn(List.of());

        mvc.perform(get("/api/residues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(List.of())));

        verify(residueService, times(1)).getAllResidues();
    }

    @Test
    void getResidue_shouldReturn404WhenResidueDoesntExists() throws Exception {
        when(residueService.getResidue(0L))
                .thenReturn(Optional.empty());

        mvc.perform(get("/api/residues/0"))
                .andExpect(status().isNotFound());

        verify(residueService, times(1)).getResidue(0L);
    }

    @Test
    void getResidue_shouldReturnResidue() throws Exception {
        Residue residue = new Residue("Plastic", "A ton of microplastics", 1f, 1f);
        residue.setId(0L);

        when(residueService.getResidue(0L))
                .thenReturn(Optional.of(residue));

        mvc.perform(get("/api/residues/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.name", is("Plastic")));

        verify(residueService, times(1)).getResidue(0L);
    }

    @Test
    void createResidue_shouldReturn201AndId() throws Exception {
        when(residueService.createResidue(any(Residue.class))).thenReturn(5L);

        String jsonString = """
                {
                  "id": null,
                  "name": "Glass",
                  "desc": "Clear glass bottles",
                  "weight": 1.0,
                  "volume": 2.0
                }
                """;

        mvc.perform(post("/api/residues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", is(5)));

        verify(residueService, times(1)).createResidue(any(Residue.class));
    }

    @Test
    void deleteResidue_shouldDeleteResidue() throws Exception {
        mvc.perform(delete("/api/residues/5"))
                .andExpect(status().isNoContent());

        verify(residueService, times(1)).deleteResidue(5L);
    }

    @Test
    void deleteResidue_shouldReturnBadRequestWhenNotFound() throws Exception {
        doThrow(new NoSuchElementException("Residue not found"))
                .when(residueService)
                .deleteResidue(5L);

        mvc.perform(delete("/api/residues/5"))
                .andExpect(status().isBadRequest());

        verify(residueService, times(1)).deleteResidue(5L);
    }

    @Test
    void searchResidues_shouldReturnResults() throws Exception {
        Residue residue = new Residue("Metal", "Steel cans", 2f, 1f);
        residue.setId(10L);

        when(residueService.getResiduesBySimilarNameOrDesc("metal"))
                .thenReturn(List.of(residue));

        mvc.perform(get("/api/residues/search/metal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(10)))
                .andExpect(jsonPath("$[0].name", is("Metal")))
                .andExpect(jsonPath("$[0].desc", is("Steel cans")));

        verify(residueService, times(1)).getResiduesBySimilarNameOrDesc("metal");
    }

    @Test
    void searchResidues_shouldReturnEmptyListWhenNoMatches() throws Exception {
        when(residueService.getResiduesBySimilarNameOrDesc("unknown"))
                .thenReturn(List.of());

        mvc.perform(get("/api/residues/search/unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(List.of())));

        verify(residueService, times(1)).getResiduesBySimilarNameOrDesc("unknown");
    }
}
