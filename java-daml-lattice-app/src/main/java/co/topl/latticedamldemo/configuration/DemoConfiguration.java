package co.topl.latticedamldemo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;

@PropertySource({
        "classpath:daml-demo-${envTarget:local}.properties"
})
@Data
@Component
public class DemoConfiguration {

    @Value("${topl.operatoraddress}")
    private String operatorAddress;

    @Value("${daml.apiserver.host}")
    private String damlApiServerHost;

    @Value("${daml.apiserver.port}")
    private Integer damlApiServerPort;

    @Value("${daml.operatorid}")
    private String operatorId;

    @Value("${demo.appid}")
    private String appId;

    @Value("${topl.apikey}")
    private String toplApiKey;

    @Value("${topl.networkurl}")
    private String toplNetworkUrl;

    @Value("${topl.keyfile.name}")
    private String keyfileName;

    @Value("${topl.keyfile.password}")
    private String keyfilePassword;

}
