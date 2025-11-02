package zeromonos.data.statuses;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import zeromonos.data.requests.Request;
import zeromonos.data.requests.RequestStatus;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @NotNull
    @Column
    private LocalDateTime datetime;

    @NotNull
    @ManyToOne
    @JoinColumn
    private Request request;

    public Status() {}

    public Status(RequestStatus requestStatus, LocalDateTime datetime, Request request) {
        this.requestStatus = requestStatus;
        this.datetime = datetime;
        this.request = request;
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

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public Request getRequest() {
        return request;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return Objects.equals(getId(), status.getId()) && getRequestStatus() == status.getRequestStatus() && Objects.equals(getDatetime(), status.getDatetime()) && Objects.equals(getRequest(), status.getRequest());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getRequestStatus(), getDatetime());
    }

}
