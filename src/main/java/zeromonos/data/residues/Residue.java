package zeromonos.data.residues;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import zeromonos.data.requests.Request;

import java.util.Objects;

@Entity
public class Residue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column
    @Length(max = 30)
    private String name;

    @Column
    @Length(max = 100)
    private String desc;

    @NotNull
    @Column
    @Positive
    private Float weight;

    @NotNull
    @Column
    @Positive
    private Float volume;

    @ManyToOne
    @JoinColumn
    private Request request;

    // Constructor

    public Residue() {}

    public Residue(String name,  String desc, Float weight, Float volume) {
        this(name, weight, volume);
        this.desc = desc;
    }

    public Residue(String name, Float weight, Float volume) {
        this.name = name;
        this.weight = weight;
        this.volume = volume;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Float getWeight() {
        return weight;
    }

    public Float getVolume() {
        return volume;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Residue residue = (Residue) o;
        return Objects.equals(id, residue.id) && Objects.equals(getName(), residue.getName()) && Objects.equals(getDesc(), residue.getDesc()) && Objects.equals(getWeight(), residue.getWeight()) && Objects.equals(getVolume(), residue.getVolume()) && Objects.equals(getRequest(), residue.getRequest());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getName(), getDesc(), getWeight(), getVolume());
    }
}
