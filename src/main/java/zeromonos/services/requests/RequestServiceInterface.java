package zeromonos.services.requests;

import zeromonos.data.requests.Request;

import java.util.List;

public interface RequestServiceInterface {

    String createRequest(Request request, List<Long> residueIds);

    void deleteRequest(String token);

    Request getRequest(String token);

    void cancelRequest(String token);

    void assignRequest(String token);

    void startRequest(String token);

    void completeRequest(String token);

    List<Request> getAllRequests();

    List<Request> getAllRequestsByMunicipality(String municipality);
}
