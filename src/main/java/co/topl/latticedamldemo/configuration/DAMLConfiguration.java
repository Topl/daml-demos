package co.topl.latticedamldemo.configuration;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.daml.ledger.api.v1.admin.PartyManagementServiceOuterClass.PartyDetails;
import com.daml.ledger.javaapi.data.FiltersByParty;
import com.daml.ledger.javaapi.data.LedgerOffset;
import com.daml.ledger.javaapi.data.NoFilter;
import com.daml.ledger.javaapi.data.Transaction;
import com.daml.ledger.rxjava.DamlLedgerClient;

import akka.actor.ActorSystem;
import akka.http.javadsl.model.Uri;
import co.topl.client.Provider;
import co.topl.daml.DamlAppContext;
import co.topl.daml.ToplContext;
import io.reactivex.Flowable;

@Configuration
public class DAMLConfiguration {

    @Autowired
    DemoConfiguration demoConfiguration;

    @Autowired
    private DamlLedgerClient damlLedgerClient;

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private PartyDetails operatorParty;

    @Autowired
    private Environment env;

    @Bean
    public DamlAppContext damlAppContext() {
        return new DamlAppContext(demoConfiguration.getAppId(), operatorParty.getParty(),
                damlLedgerClient);
    }

    @Bean
    public ToplContext toplContext() {
        Uri uri = Uri.create(demoConfiguration.getToplNetworkUrl());
        if (env.getProperty("envTarget", "local").equals("local")) {
            return new ToplContext(actorSystem,
                    new Provider.PrivateTestNet(uri.asScala(), demoConfiguration.getToplApiKey()));
        } else if (env.getProperty("envTarget").equals("valhalla")) {
            return new ToplContext(actorSystem,
                    new Provider.ValhallaTestNet(uri.asScala(), demoConfiguration.getToplApiKey()));
        } else {
            return new ToplContext(actorSystem,
                    new Provider.ToplMainNet(uri.asScala(), demoConfiguration.getToplApiKey()));
        }
    }

    @Bean
    public Flowable<Transaction> transactions() {
        return damlLedgerClient.getTransactionsClient().getTransactions(
                LedgerOffset.LedgerEnd.getInstance(),
                new FiltersByParty(Collections.singletonMap(operatorParty.getParty(), NoFilter.instance)),
                true);
    }

}
