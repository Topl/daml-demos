package co.topl.latticedamldemo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.daml.ledger.rxjava.DamlLedgerClient;

import akka.actor.ActorSystem;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class BaseDAMLConfiguration {

    @Autowired
    DemoConfiguration demoConfiguration;

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress(
                demoConfiguration.getDamlApiServerHost(), demoConfiguration.getDamlApiServerPort())
                .usePlaintext().build();
    }

    @Bean
    public DamlLedgerClient damlLedgerClient() {
        DamlLedgerClient client = DamlLedgerClient
                .newBuilder(demoConfiguration.getDamlApiServerHost(), demoConfiguration.getDamlApiServerPort())
                .build();
        client.connect();
        return client;
    }

    @Bean
    public ActorSystem actorSystem() {
        return ActorSystem.create();
    }

}
