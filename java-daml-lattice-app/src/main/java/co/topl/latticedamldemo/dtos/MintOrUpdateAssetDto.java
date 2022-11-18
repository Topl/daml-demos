package co.topl.latticedamldemo.dtos;

import lombok.Data;

@Data
public class MintOrUpdateAssetDto {

    /**
     * Id of the inventory entry.
     */
    private String iouIdentifier;

    /**
     * Id of the asset that has this inventory.
     */
    private Long assetId;

    private String shortName;

    private Long quantity;

    private Long orgId;

    private String metadata;

    private String contractId;

}
