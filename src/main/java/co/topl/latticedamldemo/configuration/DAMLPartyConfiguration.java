package co.topl.latticedamldemo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.daml.ledger.api.v1.admin.PartyManagementServiceGrpc.PartyManagementServiceBlockingStub;
import com.daml.ledger.api.v1.admin.PartyManagementServiceOuterClass.AllocatePartyRequest;
import com.daml.ledger.api.v1.admin.PartyManagementServiceOuterClass.AllocatePartyResponse;
import com.daml.ledger.api.v1.admin.PartyManagementServiceOuterClass.PartyDetails;
import com.daml.ledger.api.v1.admin.UserManagementServiceGrpc.UserManagementServiceBlockingStub;
import com.daml.ledger.api.v1.admin.UserManagementServiceOuterClass.CreateUserRequest;
import com.daml.ledger.api.v1.admin.UserManagementServiceOuterClass.User;

import co.topl.latticedamldemo.LatticeDamlDemoApplication;

@Configuration
public class DAMLPartyConfiguration {

        @Autowired
        private PartyManagementServiceBlockingStub partyManagementService;

        @Autowired
        private UserManagementServiceBlockingStub userManagementService;

        @Bean
        public PartyDetails operatorParty() {
                AllocatePartyResponse getPartyResponse = partyManagementService
                                .allocateParty(AllocatePartyRequest.newBuilder()
                                                .setDisplayName(LatticeDamlDemoApplication.OPERATOR_ID)
                                                .setPartyIdHint(LatticeDamlDemoApplication.OPERATOR_ID).build());

                userManagementService.createUser(
                                CreateUserRequest.newBuilder()
                                                .setUser(
                                                                User.newBuilder()
                                                                                .setId(LatticeDamlDemoApplication.OPERATOR_ID)
                                                                                .setPrimaryParty(getPartyResponse
                                                                                                .getPartyDetails()
                                                                                                .getParty())
                                                                                .build())
                                                .build());
                return getPartyResponse.getPartyDetails();
        }

}
