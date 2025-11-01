package zeromonos.data.statusses;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import zeromonos.data.requests.Request;
import zeromonos.data.requests.RequestStatus;

import java.time.LocalDateTime;

@Entity
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @NotNull
    @Column
    private LocalDateTime datetime;

    @NotNull
    @ManyToOne
    @JoinColumn
    private Request request;

    public Status() {}

    public Status(RequestStatus status, LocalDateTime datetime,  Request request) {
        this.status = status;
        this.datetime = datetime;
        this.request = request;
    }

}
