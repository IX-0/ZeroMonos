package zeromonos.services;

import zeromonos.data.requests.Request;

public interface RequestServiceInterface {

    public String createRequest(Request request);

    public Request getRequest(String token);


    public void cancelRequest(String token);

    public void assignRequest(String token);

    public void startRequest(String token);

    public void completeRequest(String token);
}
