package broker

import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import com.daml.ledger.rxjava.DamlLedgerClient
import com.daml.ledger.rxjava.UserManagementClient
import com.daml.ledger.javaapi.data.GetUserRequest
import akka.http.scaladsl.model.Uri
import co.topl.daml.DamlAppContext
import co.topl.daml.ToplContext
import akka.actor.ActorSystem
import co.topl.client.Provider
import co.topl.daml.polys.processors.TransferRequestProcessor
import com.daml.ledger.javaapi.data.LedgerOffset
import com.daml.ledger.javaapi.data.FiltersByParty
import java.util.Collections
import com.daml.ledger.javaapi.data.NoFilter
import io.reactivex.Flowable
import com.daml.ledger.javaapi.data.Transaction
import co.topl.daml.polys.processors.SignedTransferProcessor
import co.topl.daml.polys.processors.UnsignedTransferProcessor
import co.topl.daml.api.model.topl.transfer.UnsignedTransfer
import co.topl.daml.assets.processors.AssetMintingRequestProcessor
import co.topl.daml.assets.processors.SignedMintingRequestProcessor
import co.topl.daml.assets.processors.UnsignedMintingRequestProcessor
import co.topl.daml.api.model.topl.asset.UnsignedAssetMinting
import co.topl.daml.assets.processors.AssetTransferRequestProcessor
import co.topl.daml.assets.processors.SignedAssetTransferRequestProcessor

object BrokerMain
    extends IOApp
    with PolyProcessorRegistrationModule
    with AssetTransferProcessorRegistrationModule
    with AssetMintingProcessorRegistrationModule
    with BalanceProcessorModule {

  val EXPECTED_ARG_COUNT = 4;
  val OPERATOR_USER = "public";
  val APP_ID = "toplBrokerApp";

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      tuple <- getArgs(args)
      (host, port, keyfile, password) = tuple
      client <- createClient(host, port)
      _ <- connect(client)
      userManagementClient <- getUserManagementClient(client)
      operatorParty <- getOperatorParty(userManagementClient)
      _ <- IO.println("The operator party is: " + operatorParty)
      uri <- IO(Uri("http://localhost:9085/"))
      damlAppContext <- IO(new DamlAppContext(APP_ID, operatorParty, client))
      toplContext <- IO(
        new ToplContext(
          ActorSystem(),
          new Provider.PrivateTestNet(uri, "")
        )
      )
      transactions <- getTransactions(client, operatorParty)
    } yield {
      implicit val damlAppContextImpl = damlAppContext
      implicit val toplContextImpl = toplContext
      implicit val transactionsImpl = transactions
      for {
        _ <- registerTransferProcessor()
        _ <- registerSignedTransferProcessor()
        _ <- registerUnsignedTransferProcessor(keyfile, password)
        _ <- registerUnsignedMintingProcessor(keyfile, password)
        _ <- registerMintingRequestProcessor()
        _ <- registerAssetTransferRequestProcessor()
        _ <- registerSignedMintingRequestProcessor()
        _ <- registerSignedAssetTransferRequestProcessor()
        _ <- registerAssetBalanceProcessor()
        _ <- IO.never[Unit]
      } yield ExitCode.Success
    }).flatten

  def getTransactions(client: DamlLedgerClient, operatorParty: String) = IO(
    client
      .getTransactionsClient()
      .getTransactions(
        LedgerOffset.LedgerEnd.getInstance(),
        new FiltersByParty(
          Collections.singletonMap(operatorParty, NoFilter.instance)
        ),
        true
      )
  )

  def getOperatorParty(userManagementClient: UserManagementClient) = for {
    getUserResponse <- IO.blocking(
      userManagementClient
        .getUser(new GetUserRequest(OPERATOR_USER))
        .blockingGet()
    )
  } yield getUserResponse.getUser().getPrimaryParty().get()

  def getUserManagementClient(client: DamlLedgerClient) =
    IO(client.getUserManagementClient())

  def createClient(host: String, port: Int) = IO(
    DamlLedgerClient.newBuilder(host, port).build()
  )

  def connect(client: DamlLedgerClient) = IO(client.connect())

  def getArgs(args: List[String]) = if (args.size != EXPECTED_ARG_COUNT) {
    for {
      _ <- IO.print("Usage: HOST PORT KEYFILENAME KEYFILEPASSWORD")
    } yield throw new IllegalArgumentException
  } else {
    val host = args(0)
    val port = Integer.parseInt(args(1))
    val keyfile = args(2)
    val password = args(3)
    IO.pure((host, port, keyfile, password))
  }

}
