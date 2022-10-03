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

@Configuration
public class DAMLPartyConfiguration {

        @Autowired
        DemoConfiguration demoConfiguration;

        @Autowired
        private PartyManagementServiceBlockingStub partyManagementService;

        @Autowired
        private UserManagementServiceBlockingStub userManagementService;

        @Bean
        public PartyDetails operatorParty() {
                AllocatePartyResponse getPartyResponse = partyManagementService
                                .allocateParty(AllocatePartyRequest.newBuilder()
                                                .setDisplayName(demoConfiguration.getOperatorId())
                                                .setPartyIdHint(demoConfiguration.getOperatorId()).build());

                userManagementService.createUser(
                                CreateUserRequest.newBuilder()
                                                .setUser(
                                                                User.newBuilder()
                                                                                .setId(demoConfiguration
                                                                                                .getOperatorId())
                                                                                .setPrimaryParty(getPartyResponse
                                                                                                .getPartyDetails()
                                                                                                .getParty())
                                                                                .build())
                                                .build());
                return getPartyResponse.getPartyDetails();
        }

}
