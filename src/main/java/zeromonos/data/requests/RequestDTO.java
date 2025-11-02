package zeromonos.data.requests;

import zeromonos.data.residues.Residue;
import zeromonos.data.residues.ResidueDTO;
import zeromonos.data.statuses.Status;
import zeromonos.data.statuses.StatusDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequestDTO {

    private String token;
    private RequestStatus requestStatus = RequestStatus.RECEIVED;
    private String municipality;
    private LocalDateTime datetime;
    private List<ResidueDTO> residues = new ArrayList<>();
    private List<StatusDTO> statuses = new ArrayList<>();

    public static Request toRequestEntity(RequestDTO requestDTO) {
        return new Request(requestDTO.municipality, requestDTO.datetime);
    }

    public static RequestDTO fromRequestEntity(Request request) {
        return new RequestDTO(request.getToken(), request.getRequestStatus(), request.getMunicipality(), request.getDatetime(), request.getResidues(), request.getStatuses());
    }

    public RequestDTO() {}

    public RequestDTO(String token, RequestStatus requestStatus, String municipality, LocalDateTime datetime, List<Residue> residues, List<Status> statuses) {
        this.token = token;
        this.requestStatus = requestStatus;
        this.municipality = municipality;
        this.datetime = datetime;
        this.residues.addAll(residues.stream().map(ResidueDTO::fromResidueEntity).toList());
        this.statuses.addAll(statuses.stream().map(StatusDTO::fromStatusEntity).toList());
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

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public List<ResidueDTO> getResidues() {
        return residues;
    }

    public void setResidues(List<ResidueDTO> residues) {
        this.residues = residues;
    }

    public List<StatusDTO> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<StatusDTO> statuses) {
        this.statuses = statuses;
    }
}
