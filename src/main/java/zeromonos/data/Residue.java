package zeromonos.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import zeromonos.data.requests.Request;

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
    private Float Volume;

    @ManyToOne
    @JoinColumn
    private Request request;

    // Getters and setters

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
        return Volume;
    }

    public void setVolume(Float volume) {
        Volume = volume;
    }
}
