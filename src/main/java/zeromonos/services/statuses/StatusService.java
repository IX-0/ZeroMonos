package zeromonos.services.statuses;

import org.springframework.stereotype.Service;
import zeromonos.data.requests.RequestStatus;
import zeromonos.data.statuses.Status;
import zeromonos.data.statuses.StatusRepository;

import java.util.List;

@Service
public class StatusService implements StatusServiceInterface {

    private final StatusRepository statusRepository;

    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @Override
    public List<Status> getStatus(String requestToken, RequestStatus requestStatus) {
        return statusRepository.findAllByRequest_TokenAndRequestStatusEquals(requestToken, requestStatus);
    }

    @Override
    public List<Status> getAllStatuses(String requestToken) {
        return statusRepository.findAllByRequest_Token(requestToken);
    }
}
