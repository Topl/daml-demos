package co.topl.latticedamldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = {
		MongoAutoConfiguration.class,
		MongoDataAutoConfiguration.class
})
public class LatticeDamlDemoApplication {

	// FIXME: have all of these parameters passed either as a file or as command
	// line parameters

	public static final String OPERATOR_ADDRESS = "<operator address>";

	public static final String DAML_API_SERVER = "localhost";

	public static final Integer DAML_API_SERVER_PORT = 6865;

	public static final String OPERATOR_ID = "operator";

	public static final String APP_ID = "lattice_demo_app";

	public static final String TOPL_API_KEY = "<topl API key>";

	public static final String TOPL_NETWORK_URL = "https://vertx.topl.services/valhalla/<project id>";

	public static final String KEYFILE_NAME = "keyfile.json";

	public static final String KEYFILE_PASSWORD = "<password>";

	public static void main(String[] args) {
		SpringApplication.run(LatticeDamlDemoApplication.class, args);
	}
}
