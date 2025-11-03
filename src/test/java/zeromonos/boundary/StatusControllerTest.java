package zeromonos.boundary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import zeromonos.data.requests.Request;
import zeromonos.data.requests.RequestStatus;
import zeromonos.data.statuses.Status;
import zeromonos.services.statuses.StatusService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(controllers = StatusController.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private StatusService statusService;

    @Test
    void getAllStatuses_shouldReturnListOfStatuses() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Request request = new Request("Aveiro", now);
        request.setToken("token123");

        Status status = new Status(RequestStatus.RECEIVED, now, request);
        status.setId(1L);

        when(statusService.getAllStatuses("token123")).thenReturn(List.of(status));

        mvc.perform(get("/api/statuses/request/token123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].requestStatus", is("RECEIVED")))
                .andExpect(jsonPath("$[0].requestToken", is("token123")));

        verify(statusService, times(1)).getAllStatuses("token123");
    }

    @Test
    void getAllStatuses_shouldReturnEmptyList() throws Exception {
        when(statusService.getAllStatuses("token123")).thenReturn(List.of());

        mvc.perform(get("/api/statuses/request/token123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(List.of())));

        verify(statusService, times(1)).getAllStatuses("token123");
    }

    @Test
    void getStatusesByType_shouldReturnFilteredStatuses() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Request request = new Request("Aveiro", now);
        request.setToken("token123");

        Status status = new Status(RequestStatus.RECEIVED, now, request);
        status.setId(5L);

        when(statusService.getStatus("token123", RequestStatus.RECEIVED))
                .thenReturn(List.of(status));

        mvc.perform(get("/api/statuses/request/token123/filter/RECEIVED"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id", is(5)))
                .andExpect(jsonPath("$[0].requestStatus", is("RECEIVED")))
                .andExpect(jsonPath("$[0].requestToken", is("token123")));

        verify(statusService, times(1)).getStatus("token123", RequestStatus.RECEIVED);
    }

    @Test
    void getStatusesByType_shouldReturnEmptyList() throws Exception {
        when(statusService.getStatus("token123", RequestStatus.CANCELED))
                .thenReturn(List.of());

        mvc.perform(get("/api/statuses/request/token123/filter/CANCELED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(List.of())));

        verify(statusService, times(1)).getStatus("token123", RequestStatus.CANCELED);
    }
}
