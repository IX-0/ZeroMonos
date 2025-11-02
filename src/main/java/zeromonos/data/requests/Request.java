package zeromonos.data.requests;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import zeromonos.data.residues.Residue;
import zeromonos.data.statuses.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private List<Status> statuses = new ArrayList<>();

    @NotNull
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Residue> residues = new ArrayList<>();

    // Constructors

    public Request() {}

    public Request(String municipality, LocalDateTime datetime) {
        this.municipality = municipality;
        this.datetime = datetime;
        this.statuses.add(new Status(RequestStatus.RECEIVED, datetime, this));
    }

    // Add/Remove residues

    public void addResidue(Residue residue) {
        residues.add(residue);
        residue.setRequest(this);
    }

    public void removeResidue(Residue residue) {
        residues.remove(residue);
        residue.setRequest(null);
    }

    // Transient state

    @Transient
    private RequestState state;

    @PostLoad
    @PostPersist
    private void initState() {
        this.state = RequestStateFactory.getState(this);
    }

    // Expose state behavior

    public Request assign() {
        getState().assign();
        this.statuses.add(new Status(RequestStatus.ASSIGNED, LocalDateTime.now(), this));
        return this;
    }

    public Request start() {
        getState().start();
        this.statuses.add(new Status(RequestStatus.IN_PROGRESS, LocalDateTime.now(), this));
        return this;
    }

    public Request complete() {
        getState().complete();
        this.statuses.add(new Status(RequestStatus.COMPLETED, LocalDateTime.now(), this));
        return this;
    }

    public Request cancel() {
        getState().cancel();
        this.statuses.add(new Status(RequestStatus.CANCELED, LocalDateTime.now(), this));
        return this;
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

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public List<Residue> getResidues() {
        return residues;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(getToken(), request.getToken())
                && getRequestStatus() == request.getRequestStatus()
                && Objects.equals(getMunicipality(), request.getMunicipality())
                && Objects.equals(getDatetime(), request.getDatetime())
                && Objects.equals(getStatuses(), request.getStatuses())
                && Objects.equals(getResidues(), request.getResidues())
                && Objects.equals(getState(), request.getState());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getToken(), getRequestStatus(), getMunicipality(), getDatetime(), getState());
    }
}
