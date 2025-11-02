package zeromonos.services.statuses;

import zeromonos.data.requests.RequestStatus;
import zeromonos.data.statuses.Status;

import java.util.List;

public interface StatusServiceInterface {

    public List<Status> getStatus(String requestToken, RequestStatus requestStatus);

    public List<Status> getAllStatuses(String requestToken);

}
