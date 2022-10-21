package co.topl.latticedamldemo.dtos;

import lombok.Data;

@Data
public class AddMemberToOrgDto {

    private String orgId;

    private String member;

    private String orgName;

}
