package co.topl.latticedamldemo.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class AssetCodeInventoryEntry {

    private @Id String iouIdentifier;

    private String shortName;

    private Long quantity;

    private String metadata;

    private Long boxNonce;

    private String contractId;

}
