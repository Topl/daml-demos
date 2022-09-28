package co.topl.latticedamldemo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.daml.ledger.api.v1.admin.PartyManagementServiceOuterClass.PartyDetails;
import com.daml.ledger.rxjava.DamlLedgerClient;

import akka.actor.ActorSystem;
import akka.http.javadsl.model.Uri;
import co.topl.client.Provider;
import co.topl.daml.DamlAppContext;
import co.topl.daml.ToplContext;
import co.topl.latticedamldemo.LatticeDamlDemoApplication;

@Configuration
public class DAMLConfiguration {

    @Autowired
    private DamlLedgerClient damlLedgerClient;

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private PartyDetails operatorParty;

    @Bean
    public DamlAppContext damlAppContext() {
        return new DamlAppContext(LatticeDamlDemoApplication.APP_ID, operatorParty.getParty(),
                damlLedgerClient);
    }

    @Bean
    public ToplContext toplContext() {
        Uri uri = Uri.create(LatticeDamlDemoApplication.TOPL_NETWORK_URL);
        return new ToplContext(actorSystem,
                new Provider.ValhallaTestNet(uri.asScala(), LatticeDamlDemoApplication.TOPL_API_KEY));
    }

}
