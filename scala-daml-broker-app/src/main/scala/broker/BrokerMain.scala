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

object BrokerMain extends IOApp {

  val EXPECTED_ARG_COUNT = 4;
  val OPERATOR_USER = "public";
  val APP_ID = "toplBrokerApp";

  override def run(args: List[String]): IO[ExitCode] =
    for {
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
      _ <- registerTransferProcessor(transactions, damlAppContext, toplContext)
      _ <- registerSignedTransferProcessor(
        transactions,
        damlAppContext,
        toplContext
      )
      _ <- registerUnsignedTransferProcessor(
        transactions,
        damlAppContext,
        toplContext,
        keyfile,
        password
      )
      _ <- registerUnsignedMintingProcessor(
        transactions,
        damlAppContext,
        toplContext,
        keyfile,
        password
      )
      _ <- registerMintingRequestProcessor(
        transactions,
        damlAppContext,
        toplContext
      )
      _ <- registerAssetTransferRequestProcessor(
        transactions,
        damlAppContext,
        toplContext
      )
      _ <- registerSignedMintingRequestProcessor(
        transactions,
        damlAppContext,
        toplContext
      )
      _ <- registerSignedAssetTransferRequestProcessor(
        transactions,
        damlAppContext,
        toplContext
      )
      _ <- IO.never[Unit]
    } yield ExitCode.Success

  def registerAssetTransferRequestProcessor(
      transactions: Flowable[Transaction],
      damlAppContext: DamlAppContext,
      toplContext: ToplContext
  ) =
    for {
      transferProcessor <- IO(
        new AssetTransferRequestProcessor(
          damlAppContext,
          toplContext,
          3000,
          (x, y) => true,
          t => true
        )
      )
      _ <- IO(transactions.forEach(transferProcessor.processTransaction))
    } yield ()

  def registerMintingRequestProcessor(
      transactions: Flowable[Transaction],
      damlAppContext: DamlAppContext,
      toplContext: ToplContext
  ) =
    for {
      transferProcessor <- IO(
        new AssetMintingRequestProcessor(
          damlAppContext,
          toplContext,
          3000,
          (x, y) => true,
          t => true
        )
      )
      _ <- IO(transactions.forEach(transferProcessor.processTransaction))
    } yield ()

  def registerSignedMintingRequestProcessor(
      transactions: Flowable[Transaction],
      damlAppContext: DamlAppContext,
      toplContext: ToplContext
  ) = {
    var i = 0
    for {
      transferProcessor <- IO(
        new SignedMintingRequestProcessor(
          damlAppContext,
          toplContext,
          3000,
          1,
          () => {
            i = i + 1
            i.toString()
          },
          (x, y) => true,
          t => true
        )
      )
      _ <- IO(transactions.forEach(transferProcessor.processTransaction))
    } yield ()
  }
  def registerSignedAssetTransferRequestProcessor(
      transactions: Flowable[Transaction],
      damlAppContext: DamlAppContext,
      toplContext: ToplContext
  ) = {
    var i = 0
    for {
      transferProcessor <- IO(
        new SignedAssetTransferRequestProcessor(
          damlAppContext,
          toplContext,
          3000,
          1,
          () => {
            i = i + 1
            i.toString()
          },
          (x, y) => true,
          t => true
        )
      )
      _ <- IO(transactions.forEach(transferProcessor.processTransaction))
    } yield ()
  }

  def registerTransferProcessor(
      transactions: Flowable[Transaction],
      damlAppContext: DamlAppContext,
      toplContext: ToplContext
  ) =
    for {
      transferProcessor <- IO(
        new TransferRequestProcessor(
          damlAppContext,
          toplContext,
          3000,
          (x, y) => true,
          t => true
        )
      )
      _ <- IO(transactions.forEach(transferProcessor.processTransaction))
    } yield ()

  def registerUnsignedMintingProcessor(
      transactions: Flowable[Transaction],
      damlAppContext: DamlAppContext,
      toplContext: ToplContext,
      keyfile: String,
      password: String
  ) =
    for {
      transferProcessor <- IO(
        new UnsignedMintingRequestProcessor(
          damlAppContext,
          toplContext,
          keyfile,
          password,
          (x: UnsignedAssetMinting, y: UnsignedAssetMinting.ContractId) => true,
          (t: Throwable) => true
        )
      )
      _ <- IO(transactions.forEach(transferProcessor.processTransaction))
    } yield ()
  def registerUnsignedTransferProcessor(
      transactions: Flowable[Transaction],
      damlAppContext: DamlAppContext,
      toplContext: ToplContext,
      keyfile: String,
      password: String
  ) =
    for {
      transferProcessor <- IO(
        new UnsignedTransferProcessor(
          damlAppContext,
          toplContext,
          keyfile,
          password
        )
      )
      _ <- IO(transactions.forEach(transferProcessor.processTransaction))
    } yield ()

  def registerSignedTransferProcessor(
      transactions: Flowable[Transaction],
      damlAppContext: DamlAppContext,
      toplContext: ToplContext
  ) =
    for {
      transferProcessor <- IO(
        new SignedTransferProcessor(
          damlAppContext,
          toplContext,
          3000,
          1,
          (x, y) => true,
          t => true
        )
      )
      _ <- IO(transactions.forEach(transferProcessor.processTransaction))
    } yield ()

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
