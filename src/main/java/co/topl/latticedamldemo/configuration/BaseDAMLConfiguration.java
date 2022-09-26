package co.topl.latticedamldemo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.daml.ledger.rxjava.DamlLedgerClient;

import akka.actor.ActorSystem;
import co.topl.latticedamldemo.LatticeDamlDemoApplication;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class BaseDAMLConfiguration {

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress(
                LatticeDamlDemoApplication.DAML_API_SERVER, LatticeDamlDemoApplication.DAML_API_SERVER_PORT)
                .usePlaintext().build();
    }

    @Bean
    public DamlLedgerClient damlLedgerClient() {
        DamlLedgerClient client = DamlLedgerClient
                .newBuilder(LatticeDamlDemoApplication.DAML_API_SERVER, LatticeDamlDemoApplication.DAML_API_SERVER_PORT)
                .build();
        client.connect();
        return client;
    }

    @Bean
    public ActorSystem actorSystem() {
        return ActorSystem.create();
    }

}
