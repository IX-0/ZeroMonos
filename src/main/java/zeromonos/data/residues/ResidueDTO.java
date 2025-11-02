package zeromonos.data.residues;

import zeromonos.data.requests.Request;

public class ResidueDTO {

    private Long id;
    private String name;
    private String desc;
    private Float weight;
    private Float volume;
    private String requestToken;

    public static ResidueDTO fromResidueEntity(Residue residue) {
        return new ResidueDTO(residue.getId(), residue.getName(), residue.getDesc(), residue.getWeight(), residue.getVolume(), residue.getRequest());
    }

    public static Residue toResidueEntity(ResidueDTO residueDTO) {
        Residue residue;
        if (residueDTO.getDesc() == null) {
            residue = new Residue(residueDTO.getName(), residueDTO.getWeight(), residueDTO.getVolume());
        } else {
            residue = new Residue(residueDTO.getName(), residueDTO.getDesc(),  residueDTO.getWeight(), residueDTO.getVolume());
        }

        return residue;
    }

    public ResidueDTO() {}

    public ResidueDTO(Long id, String name, String desc, Float weight, Float volume, Request request) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.weight = weight;
        this.volume = volume;
        this.requestToken = request != null ? request.getToken() : null ;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

}
