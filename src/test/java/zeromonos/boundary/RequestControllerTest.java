package zeromonos.boundary;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import zeromonos.data.requests.Request;
import zeromonos.data.residues.Residue;
import zeromonos.services.requests.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestController.class)
public class RequestControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    RequestService requestService;

    @Test
    void getAllRequests_shouldReturnList() throws Exception {
        Request request = new Request();
        request.setToken("token123");

        when(requestService.getAllRequests())
                .thenReturn(List.of(request));

        mvc.perform(get("/api/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].token", is("token123")));
    }

    @Test
    void getAllRequests_shouldReturnEmptyList() throws Exception {
        when(requestService.getAllRequests())
                .thenReturn(List.of());

        mvc.perform(get("/api/requests"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getAllRequestsByMunicipality_shouldReturnList() throws Exception {
        Request request0 = new Request("Aveiro", LocalDateTime.now());
        request0.setToken("token123");

        when(requestService.getAllRequestsByMunicipality("Aveiro"))
                .thenReturn(List.of(request0));

        mvc.perform(get("/api/requests/municipality/Aveiro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].token", is("token123")));
    }

    @Test
    void getAllRequestsByMunicipality_shouldReturnEmptyList() throws Exception {
        when(requestService.getAllRequestsByMunicipality("Aveiro"))
                .thenReturn(List.of());

        mvc.perform(get("/api/requests/municipality/Aveiro"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void createRequest_shouldReturnTokenOnSuccess() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        when(requestService.createRequest(Mockito.any(), Mockito.any()))
                .thenReturn("token123");

        String jsonString = """
        {
          "token": "token123",
          "requestStatus": "RECEIVED",
          "municipality": "Aveiro",
          "datetime": "%s",
          "residues": [{"id":1}]
        }
        """.formatted(now.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        mvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", is("token123")));

        verify(requestService, times(1)).createRequest(Mockito.any(), Mockito.any());
    }

    @Test
    void createRequest_shouldNotReturnTokenOnFailure() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        when(requestService.createRequest(Mockito.any(), Mockito.any()))
                .thenThrow(NoSuchElementException.class);

        String jsonString = """
        {
          "token": "token123",
          "requestStatus": "RECEIVED",
          "municipality": "Aveiro",
          "datetime": "%s",
          "residues": [{"id":1}]
        }
        """.formatted(now.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        mvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).createRequest(Mockito.any(), Mockito.any());
    }

    @Test
    void createRequest_shouldNotReturnTokenWhenIdsAreEmpty() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        when(requestService.createRequest(Mockito.any(), Mockito.any()))
                .thenThrow(IllegalArgumentException.class);

        String jsonString = """
        {
          "token": "token123",
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

        verify(requestService, times(1)).createRequest(Mockito.any(), Mockito.any());
    }

    @Test
    void getRequest_shouldReturn404WhenRequestDoesntExists() throws Exception {
        when(requestService.getRequest("token123"))
                .thenThrow(NoSuchElementException.class);

        mvc.perform(get("/api/requests/token123"))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).getRequest("token123");
    }

    @Test
    void getRequest_shouldReturnRequest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Request request = new Request("Aveiro", now);
        request.setToken("token123");

        Residue residue = new Residue("Plastic", "A ton of microplastics", 1f, 1f);
        residue.setId(0L);
        request.addResidue(residue);

        when(requestService.getRequest("token123"))
                .thenReturn(request);

        mvc.perform(get("/api/requests/token123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("token123")))
                .andExpect(jsonPath("$.requestStatus", is("RECEIVED")))
                .andExpect(jsonPath("$.municipality", is("Aveiro")))
                .andExpect(jsonPath("$.datetime", is(now.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.residues[0].id", is(0)));

        verify(requestService, times(1)).getRequest("token123");
    }

    @Test
    void deleteRequest_shouldReturn404WhenRequestDoesntExists() throws Exception {
        doThrow(NoSuchElementException.class).when(requestService).deleteRequest("token123");

        mvc.perform(delete("/api/requests/token123"))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).deleteRequest("token123");
    }

    @Test
    void deleteRequest_shouldCallDeleteRequest() throws Exception {
        mvc.perform(delete("/api/requests/token123"))
                .andExpect(status().isNoContent());

        verify(requestService, times(1)).deleteRequest("token123");
    }

    @Test
    void assignRequest_shouldReturn404WhenRequestDoesntExists() throws Exception {
        doThrow(NoSuchElementException.class).when(requestService).assignRequest("token123");

        mvc.perform(put("/api/requests/token123/assign"))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).assignRequest("token123");
    }

    @Test
    void assignRequest_shouldReturn405WhenIllegalTransition() throws Exception {
        doThrow(IllegalStateException.class).when(requestService).assignRequest("token123");

        mvc.perform(put("/api/requests/token123/assign"))
                .andExpect(status().isMethodNotAllowed());

        verify(requestService, times(1)).assignRequest("token123");
    }

    @Test
    void assignRequest_shouldCallServiceAssign() throws Exception {
        mvc.perform(put("/api/requests/token456/assign"))
                .andExpect(status().isOk());

        verify(requestService, times(1)).assignRequest("token456");
    }

    @Test
    void startRequest_shouldReturn404WhenRequestDoesntExists() throws Exception {
        doThrow(NoSuchElementException.class).when(requestService).startRequest("token123");

        mvc.perform(put("/api/requests/token123/start"))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).startRequest("token123");
    }

    @Test
    void startRequest_shouldReturn405WhenIllegalTransition() throws Exception {
        doThrow(IllegalStateException.class).when(requestService).startRequest("token123");

        mvc.perform(put("/api/requests/token123/start"))
                .andExpect(status().isMethodNotAllowed());

        verify(requestService, times(1)).startRequest("token123");
    }

    @Test
    void startRequest_shouldCallServiceStart() throws Exception {
        mvc.perform(put("/api/requests/token456/start"))
                .andExpect(status().isOk());

        verify(requestService, times(1)).startRequest("token456");
    }

    @Test
    void completeRequest_shouldReturn404WhenRequestDoesntExists() throws Exception {
        doThrow(NoSuchElementException.class).when(requestService).completeRequest("token123");

        mvc.perform(put("/api/requests/token123/complete"))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).completeRequest("token123");
    }

    @Test
    void completeRequest_shouldReturn405WhenIllegalTransition() throws Exception {
        doThrow(IllegalStateException.class).when(requestService).completeRequest("token123");

        mvc.perform(put("/api/requests/token123/complete"))
                .andExpect(status().isMethodNotAllowed());

        verify(requestService, times(1)).completeRequest("token123");
    }

    @Test
    void completeRequest_shouldCallServiceComplete() throws Exception {
        mvc.perform(put("/api/requests/token456/complete"))
                .andExpect(status().isOk());

        verify(requestService, times(1)).completeRequest("token456");
    }

    @Test
    void cancelRequest_shouldReturn404WhenRequestDoesntExists() throws Exception {
        doThrow(NoSuchElementException.class).when(requestService).cancelRequest("token123");

        mvc.perform(put("/api/requests/token123/cancel"))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).cancelRequest("token123");
    }

    @Test
    void cancelRequest_shouldReturn405WhenIllegalTransition() throws Exception {
        doThrow(IllegalStateException.class).when(requestService).cancelRequest("token123");

        mvc.perform(put("/api/requests/token123/cancel"))
                .andExpect(status().isMethodNotAllowed());

        verify(requestService, times(1)).cancelRequest("token123");
    }

    @Test
    void cancelRequest_shouldCallServiceCancel() throws Exception {
        mvc.perform(put("/api/requests/token456/cancel"))
                .andExpect(status().isOk());

        verify(requestService, times(1)).cancelRequest("token456");
    }
}
