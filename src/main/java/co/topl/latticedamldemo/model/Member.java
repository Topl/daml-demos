package co.topl.latticedamldemo.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Member {

    private @Id String userName;
    private String password;
    private String partyIdentifier;
    private String role;

    @SuppressWarnings("unused")
    private Member() {
    }

    public Member(String userName, String password, String role, String partyIdentifier) {
        this.userName = userName;
        this.password = password;
        this.partyIdentifier = partyIdentifier;
        this.role = role;
    }

}
