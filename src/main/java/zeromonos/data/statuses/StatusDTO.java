package zeromonos.data.statuses;

import zeromonos.data.requests.Request;
import zeromonos.data.requests.RequestStatus;

import java.time.LocalDateTime;

public class StatusDTO {
    private Long id;
    private RequestStatus requestStatus;
    private LocalDateTime datetime;
    private String requestToken;

    public static StatusDTO fromStatusEntity(Status status) {
        return new StatusDTO(status.getId(), status.getRequestStatus(), status.getDatetime(), status.getRequest());
    }

    public StatusDTO(Long id, RequestStatus requestStatus, LocalDateTime datetime, Request request) {
        this.id = id;
        this.requestStatus = requestStatus;
        this.datetime = datetime;
        this.requestToken = request != null ? request.getToken() : null ;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }
}
