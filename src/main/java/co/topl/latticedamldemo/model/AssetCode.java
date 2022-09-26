package co.topl.latticedamldemo.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class AssetCode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Integer version;

    private String shortName;

    @OneToMany(cascade = CascadeType.ALL)
    private List<AssetCodeInventoryEntry> assetCodeInventoryEntry;

}
