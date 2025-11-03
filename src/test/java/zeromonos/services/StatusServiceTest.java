package zeromonos.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import zeromonos.data.requests.RequestStatus;
import zeromonos.data.statuses.Status;
import zeromonos.data.statuses.StatusRepository;
import zeromonos.services.statuses.StatusService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StatusServiceTest {

    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private StatusService statusService;

    private List<Status> statuses;

    @BeforeEach
    void setUp() {
        Status status1 = new Status();
        status1.setId(0L);
        Status status2 = new Status();
        status2.setId(1L);

        statuses = List.of(status1, status2);

        when(statusRepository.findAllByRequest_TokenAndRequestStatusEquals("REQ123", RequestStatus.ASSIGNED))
                .thenReturn(statuses);

        when(statusRepository.findAllByRequest_Token("REQ123"))
                .thenReturn(statuses);

        when(statusRepository.findAllByRequest_TokenAndRequestStatusEquals("REQ999", RequestStatus.COMPLETED))
                .thenReturn(List.of());

        when(statusRepository.findAllByRequest_Token("REQ999"))
                .thenReturn(List.of());
    }

    @Test
    void getStatus_shouldReturnStatusesForGivenTokenAndRequestStatus() {
        List<Status> result = statusService.getStatus("REQ123", RequestStatus.ASSIGNED);

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsAll(statuses);

        verify(statusRepository, times(1))
                .findAllByRequest_TokenAndRequestStatusEquals("REQ123", RequestStatus.ASSIGNED);
    }

    @Test
    void getStatus_shouldReturnEmptyListWhenNoStatusesFound() {
        List<Status> result = statusService.getStatus("REQ999", RequestStatus.COMPLETED);

        assertThat(result).isEmpty();

        verify(statusRepository)
                .findAllByRequest_TokenAndRequestStatusEquals("REQ999", RequestStatus.COMPLETED);
    }

    @Test
    void getAllStatuses_shouldReturnAllStatusesForRequest() {
        List<Status> result = statusService.getAllStatuses("REQ123");

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsAll(statuses);

        verify(statusRepository, times(1)).findAllByRequest_Token("REQ123");
    }

    @Test
    void getAllStatuses_shouldReturnEmptyListWhenNoStatusesExist() {
        List<Status> result = statusService.getAllStatuses("REQ999");

        assertThat(result).isEmpty();
        verify(statusRepository, times(1)).findAllByRequest_Token("REQ999");
    }
}
