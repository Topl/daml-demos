package co.topl.latticedamldemo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class AssetCodeInventoryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String shortName;

    private Long quantity;

    private String metadata;

    private Long boxNonce;

    private String contractId;

}
