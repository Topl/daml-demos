package co.topl.latticedamldemo.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

import lombok.Data;

@Data
@Entity
public class Organization {

    private @Id String orgName;

    @OneToMany(cascade = CascadeType.ALL)
    private List<AssetCode> assetCodes;

    @SuppressWarnings("unused")
    private Organization() {
    }

    public Organization(String orgName) {
        this.orgName = orgName;
    }

}
