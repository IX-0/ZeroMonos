package zeromonos.data.requests;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import zeromonos.data.residues.Residue;
import zeromonos.data.statusses.Status;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Request {

    // Persisted state

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String token;

    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus = RequestStatus.RECEIVED;

    @NotNull
    @Column
    @Length(max = 50)
    private String municipality;

    @NotNull
    @Column
    private LocalDateTime datetime;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Status> statuses;

    @NotNull
    @OneToMany
    private List<Residue> residues;

    // Transient state

    @Transient
    private RequestState state;

    @PostLoad
    @PostPersist
    private void initState() {
        this.state = RequestStateFactory.getState(this);
    }

    // Expose behavior
    public void assign() {
        getState().assign();
    }

    public void start() {
        getState().start();
    }

    public void complete() {
        getState().complete();
    }

    public void cancel() {
        getState().cancel();
    }

    // Getters and Setters

    public RequestState getState() {
        if (state == null) {
            state = RequestStateFactory.getState(this);
        }
        return state;
    }

    public void setState(RequestState state) {
        this.state = state;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime date) {
        this.datetime = date;
    }
}
