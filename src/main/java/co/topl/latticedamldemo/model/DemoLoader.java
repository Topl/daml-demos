package co.topl.latticedamldemo.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.daml.ledger.api.v1.admin.PartyManagementServiceGrpc.PartyManagementServiceBlockingStub;
import com.daml.ledger.api.v1.admin.PartyManagementServiceOuterClass.PartyDetails;
import com.daml.ledger.api.v1.admin.UserManagementServiceGrpc.UserManagementServiceBlockingStub;
import com.daml.ledger.javaapi.data.Transaction;
import com.daml.ledger.rxjava.DamlLedgerClient;

import co.topl.daml.DamlAppContext;
import co.topl.daml.ToplContext;
import co.topl.daml.assets.processors.AssetMintingRequestProcessor;
import co.topl.daml.assets.processors.AssetTransferRequestProcessor;
import co.topl.daml.assets.processors.UnsignedAssetTransferRequestProcessor;
import co.topl.daml.assets.processors.UnsignedMintingRequestProcessor;
import co.topl.daml.operator.MembershipAcceptanceProcessor;
import co.topl.latticedamldemo.configuration.DemoConfiguration;
import io.grpc.ManagedChannel;
import io.reactivex.Flowable;

@Component
public class DemoLoader implements CommandLineRunner {

        private final MembersRepository repository;

        @Autowired
        DemoConfiguration demoConfiguration;

        @Autowired
        PasswordEncoder passwordEncoder;

        @Autowired
        ManagedChannel channel;

        @Autowired
        DamlLedgerClient client;

        @Autowired
        PartyManagementServiceBlockingStub partyManagementService;

        @Autowired
        UserManagementServiceBlockingStub userManagementService;

        @Autowired
        PartyDetails operatorPartyDetails;

        @Autowired
        DamlAppContext damlAppContext;

        @Autowired
        ToplContext toplContext;

        @Autowired
        Flowable<Transaction> transactions;

        @Autowired
        public DemoLoader(MembersRepository repository) {
                this.repository = repository;
        }

        @Override
        public void run(String... args) throws Exception {
                String thePassword = passwordEncoder.encode(demoConfiguration.getOperatorId());
                this.repository
                                .save(new Member(demoConfiguration.getOperatorId(), thePassword, "ADMIN",
                                                operatorPartyDetails.getParty()));
                MembershipAcceptanceProcessor membershipAcceptanceProcessor = new MembershipAcceptanceProcessor(
                                damlAppContext,
                                toplContext);
                transactions.forEach(membershipAcceptanceProcessor::processTransaction);
                AssetMintingRequestProcessor assetMintingRequestProcessor = new AssetMintingRequestProcessor(
                                damlAppContext,
                                toplContext);
                transactions.forEach(assetMintingRequestProcessor::processTransaction);
                UnsignedMintingRequestProcessor unsignedMintingRequestProcessor = new UnsignedMintingRequestProcessor(
                                damlAppContext, toplContext, demoConfiguration.getKeyfileName(),
                                demoConfiguration.getKeyfilePassword());
                transactions.forEach(unsignedMintingRequestProcessor::processTransaction);
                AssetTransferRequestProcessor assetTransferRequestProcessor = new AssetTransferRequestProcessor(
                                damlAppContext,
                                toplContext);
                transactions.forEach(assetTransferRequestProcessor::processTransaction);
                UnsignedAssetTransferRequestProcessor unsignedTransferRequestProcessor = new UnsignedAssetTransferRequestProcessor(
                                damlAppContext, toplContext, demoConfiguration.getKeyfileName(),
                                demoConfiguration.getKeyfilePassword());
                transactions.forEach(unsignedTransferRequestProcessor::processTransaction);
        }

}
