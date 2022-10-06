package co.topl.latticedamldemo.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

import lombok.Data;

@Data
@Entity
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String orgName;

    @OneToMany(cascade = CascadeType.ALL)
    private List<AssetCode> assetCodes;

    @SuppressWarnings("unused")
    private Organization() {
    }

    public Organization(String orgName) {
        this.orgName = orgName;
    }

}
