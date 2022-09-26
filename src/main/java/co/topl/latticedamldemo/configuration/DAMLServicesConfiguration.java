package co.topl.latticedamldemo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.daml.ledger.api.v1.CommandSubmissionServiceGrpc;
import com.daml.ledger.api.v1.CommandSubmissionServiceGrpc.CommandSubmissionServiceBlockingStub;
import com.daml.ledger.api.v1.admin.PartyManagementServiceGrpc;
import com.daml.ledger.api.v1.admin.PartyManagementServiceGrpc.PartyManagementServiceBlockingStub;
import com.daml.ledger.api.v1.admin.UserManagementServiceGrpc;
import com.daml.ledger.api.v1.admin.UserManagementServiceGrpc.UserManagementServiceBlockingStub;

import io.grpc.ManagedChannel;

@Configuration
public class DAMLServicesConfiguration {

    @Autowired
    private ManagedChannel channel;

    @Bean
    public PartyManagementServiceBlockingStub partyManagementService() {
        return PartyManagementServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    public UserManagementServiceBlockingStub userManagementService() {
        return UserManagementServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    public CommandSubmissionServiceBlockingStub CommandSubmissionService() {
        return CommandSubmissionServiceGrpc.newBlockingStub(channel);
    }

}
