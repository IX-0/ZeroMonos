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
import zeromonos.data.requests.RequestRepository;
import zeromonos.data.residues.Residue;
import zeromonos.data.residues.ResidueRepository;
import zeromonos.services.requests.RequestService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ResidueRepository residueRepository;

    @InjectMocks
    private RequestService requestService;

    private Request request;
    private Residue residue1;
    private Residue residue2;

    @BeforeEach
    void setUp() {
        residue1 = new Residue("Plastic", "desc", 1f, 2f);
        residue1.setId(1L);
        residue2 = new Residue("Wood", "desc", 2f, 3f);
        residue2.setId(2L);

        request = new Request();
        request.setToken("REQ123");

        when(residueRepository.findById(1L)).thenReturn(Optional.of(residue1));
        when(residueRepository.findById(2L)).thenReturn(Optional.of(residue2));
        when(residueRepository.findById(-1L)).thenReturn(Optional.empty());

        when(requestRepository.saveAndFlush(any(Request.class))).thenAnswer(inv -> {
            Request req = inv.getArgument(0);
            req.setToken("REQ123");
            return req;
        });

        when(requestRepository.findRequestByTokenEquals("REQ123")).thenReturn(Optional.of(request));
        when(requestRepository.findRequestByTokenEquals("UNKNOWN")).thenReturn(Optional.empty());

        when(requestRepository.save(any(Request.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void createRequest_shouldSaveRequestWithResidues() {
        String token = requestService.createRequest(request, List.of(1L, 2L));

        assertThat(token).isEqualTo("REQ123");
        assertThat(request.getResidues())
                .hasSize(2)
                .containsExactlyInAnyOrder(residue1, residue2);

        verify(residueRepository, times(2)).findById(anyLong());
        verify(requestRepository).saveAndFlush(request);
    }

    @Test
    void createRequest_shouldThrowWhenNoResiduesAreProvided() {
        assertThatThrownBy(() -> requestService.createRequest(request, List.of()));
        verify(requestRepository, never()).saveAndFlush(request);
    }

    @Test
    void createRequest_shouldThrowWhenResidueNotFound() {
        assertThatThrownBy(() -> requestService.createRequest(request, List.of(-1L)))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Residue with id -1 not found");

        verify(residueRepository).findById(-1L);
        verify(requestRepository, never()).saveAndFlush(any());
    }

    @Test
    void getRequest_shouldReturnRequestWhenExists() {
        Request found = requestService.getRequest("REQ123");

        assertThat(found).isNotNull();
        assertThat(found.getToken()).isEqualTo("REQ123");
        verify(requestRepository).findRequestByTokenEquals("REQ123");
    }

    @Test
    void getRequest_shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> requestService.getRequest("UNKNOWN"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Request with Token UNKNOWN not found");

        verify(requestRepository).findRequestByTokenEquals("UNKNOWN");
    }

    @Test
    void deleteRequest_shouldDeleteRequest() {
        requestService.deleteRequest("REQ123");

        verify(requestRepository, times(1)).deleteByToken("REQ123");
        verify(requestRepository, times(1)).findRequestByTokenEquals("REQ123");
    }

    @Test
    void deleteRequest_shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> requestService.deleteRequest("UNKNOWN"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Request with Token UNKNOWN not found");

        verify(requestRepository, never()).delete(any());
    }

    @Test
    void cancelRequest_shouldSaveCancelledRequest() {
        requestService.cancelRequest("REQ123");

        verify(requestRepository).save(request);
    }

    @Test
    void cancelRequest_shouldThrowWhenInvalidTransition() {
        request.assign().start();
        assertThatThrownBy(() -> requestService.cancelRequest("REQ123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Illegal state transition");

        verify(requestRepository, never()).save(request);
    }

    @Test
    void cancelRequest_shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> requestService.cancelRequest("UNKNOWN"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Request with Token UNKNOWN not found");
        verify(requestRepository, never()).save(request);
    }

    @Test
    void assignRequest_shouldSaveAssignedRequest() {
        requestService.assignRequest("REQ123");

        verify(requestRepository).save(request);
    }

    @Test
    void assignRequest_shouldThrowWhenInvalidTransition() {
        request.assign();
        assertThatThrownBy(() -> requestService.assignRequest("REQ123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Illegal state transition");

        verify(requestRepository, never()).save(request);
    }

    @Test
    void assignRequest_shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> requestService.assignRequest("UNKNOWN"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Request with Token UNKNOWN not found");
        verify(requestRepository, never()).save(request);
    }

    @Test
    void startRequest_shouldSaveStartedRequest() {
        request.assign();
        requestService.startRequest("REQ123");

        verify(requestRepository).save(request);
    }

    @Test
    void startRequest_shouldThrowWhenInvalidTransition() {
        assertThatThrownBy(() -> requestService.startRequest("REQ123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Illegal state transition");

        verify(requestRepository, never()).save(request);
    }

    @Test
    void startRequest_shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> requestService.startRequest("UNKNOWN"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Request with Token UNKNOWN not found");
        verify(requestRepository, never()).save(request);
    }

    @Test
    void completeRequest_shouldSaveCompletedRequest() {
        request.assign().start();
        requestService.completeRequest("REQ123");

        verify(requestRepository).save(request);
    }

    @Test
    void completeRequest_shouldThrowWhenInvalidTransition() {
        assertThatThrownBy(() -> requestService.completeRequest("REQ123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Illegal state transition");

        verify(requestRepository, never()).save(request);
    }

    @Test
    void completeRequest_shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> requestService.completeRequest("UNKNOWN"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Request with Token UNKNOWN not found");

        verify(requestRepository, never()).save(request);
    }
}
