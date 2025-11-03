package zeromonos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;


import zeromonos.data.requests.Request;
import zeromonos.data.requests.RequestRepository;
import zeromonos.data.residues.Residue;
import zeromonos.data.residues.ResidueRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ZeroMonosApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class RequestControllerIT {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ResidueRepository residueRepository;

    private final LocalDateTime now = LocalDateTime.now();
    private String existingToken = "";
    private Long res0id = null;

    @BeforeEach
    void setup() {
        Request r0 = new Request("Aveiro", now);

        existingToken = requestRepository.saveAndFlush(r0).getToken();

        Residue res0 = new Residue("Plastic", "A lot of microplastics", 1f, 1f);
        Residue res1 = new Residue("Wood", "My wood", 2f, 2f);

        res0id = residueRepository.saveAndFlush(res0).getId();
        residueRepository.saveAndFlush(res1);

        r0.addResidue(res0);
        r0.addResidue(res1);

        requestRepository.saveAndFlush(r0);
    }

    @AfterEach
    void tearDown() {
        requestRepository.deleteAll();
    }

    @Test
    void getAllRequests_shouldReturnList() throws Exception {
        mvc.perform(get("/api/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].token", is(existingToken)));
    }

    @Test
    void getAllRequestsByMunicipality_shouldReturnList() throws Exception {

        mvc.perform(get("/api/requests/municipality/Aveiro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].token", is(existingToken)));
    }

    @Test
    void getAllRequestsByMunicipality_shouldReturnEmptyList() throws Exception {
        mvc.perform(get("/api/requests/municipality/Porto"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void createRequest_shouldReturnTokenOnSuccess() throws Exception {
        String jsonString = """
        {
          "requestStatus": "RECEIVED",
          "municipality": "Aveiro",
          "datetime": "%s",
          "residues": [{"id":%d}]
        }
        """.formatted(now.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                res0id);

        mvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", matchesPattern(
                        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"
                )));
    }

    @Test
    void createRequest_shouldNotReturnTokenOnFailure() throws Exception {
        String jsonString = """
        {
          "requestStatus": "RECEIVED",
          "municipality": "Aveiro",
          "datetime": "%s",
          "residues": [{"id":-1}]
        }
        """.formatted(now.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        mvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRequest_shouldNotReturnTokenWhenIdsAreEmpty() throws Exception {
        String jsonString = """
        {
          "requestStatus": "RECEIVED",
          "municipality": "Aveiro",
          "datetime": "%s",
          "residues": []
        }
        """.formatted(now.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        mvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequest_shouldReturn404WhenRequestDoesntExists() throws Exception {
        mvc.perform(get("/api/requests/token456"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRequest_shouldReturnRequest() throws Exception {
        mvc.perform(get("/api/requests/%s".formatted(existingToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(existingToken)))
                .andExpect(jsonPath("$.requestStatus", is("RECEIVED")))
                .andExpect(jsonPath("$.municipality", is("Aveiro")));
    }

    @Test
    void deleteRequest_shouldReturn404WhenRequestDoesntExists() throws Exception {
        mvc.perform(delete("/api/requests/token456"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRequest_shouldCallDeleteRequest() throws Exception {
        mvc.perform(delete("/api/requests/%s".formatted(existingToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    void assignRequest_shouldReturn404WhenRequestDoesntExists() throws Exception {
        mvc.perform(put("/api/requests/token456/assign"))
                .andExpect(status().isNotFound());
    }

    @Test
    void assignRequest_shouldReturn405WhenIllegalTransition() throws Exception {
        mvc.perform(put("/api/requests/%s/assign".formatted(existingToken)))
                .andExpect(status().isOk());
        mvc.perform(put("/api/requests/%s/assign".formatted(existingToken)))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void startRequest_shouldReturn404WhenRequestDoesntExists() throws Exception {
        mvc.perform(put("/api/requests/token456/start"))
                .andExpect(status().isNotFound());
    }

    @Test
    void startRequest_shouldReturn405WhenIllegalTransition() throws Exception {
        mvc.perform(put("/api/requests/%s/start".formatted(existingToken)))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void completeRequest_shouldReturn404WhenRequestDoesntExists() throws Exception {
        mvc.perform(put("/api/requests/token456/complete"))
                .andExpect(status().isNotFound());
    }

    @Test
    void completeRequest_shouldReturn405WhenIllegalTransition() throws Exception {
        mvc.perform(put("/api/requests/%s/complete".formatted(existingToken)))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void assignAndStartAndCompleteRequest_shouldCallServiceComplete() throws Exception {
        mvc.perform(put("/api/requests/%s/assign".formatted(existingToken)))
                .andExpect(status().isOk());
        mvc.perform(put("/api/requests/%s/start".formatted(existingToken)))
                .andExpect(status().isOk());
        mvc.perform(put("/api/requests/%s/complete".formatted(existingToken)))
                .andExpect(status().isOk());
    }

    @Test
    void cancelRequest_shouldReturn404WhenRequestDoesntExists() throws Exception {
        mvc.perform(put("/api/requests/token456/cancel"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelRequest_shouldReturn405WhenIllegalTransition() throws Exception {
        mvc.perform(put("/api/requests/%s/cancel".formatted(existingToken)))
                .andExpect(status().isOk());
        mvc.perform(put("/api/requests/%s/cancel".formatted(existingToken)))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void cancelRequest_shouldCallServiceCancel() throws Exception {
        mvc.perform(put("/api/requests/%s/cancel".formatted(existingToken)))
                .andExpect(status().isOk());
    }
}
