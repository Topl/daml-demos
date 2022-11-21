package broker

import io.reactivex.Flowable
import co.topl.daml.DamlAppContext
import co.topl.daml.ToplContext

import co.topl.daml.assets.processors.AssetMintingRequestProcessor
import com.daml.ledger.javaapi.data.Transaction
import co.topl.daml.assets.processors.SignedMintingRequestProcessor
import cats.effect.IO
import co.topl.daml.assets.processors.UnsignedMintingRequestProcessor
import co.topl.daml.api.model.topl.asset.UnsignedAssetMinting

trait AssetMintingProcessorRegistrationModule {
  def registerMintingRequestProcessor()(implicit
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

  def registerSignedMintingRequestProcessor()(implicit
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

  def registerUnsignedMintingProcessor(
      keyfile: String,
      password: String
  )(implicit
      transactions: Flowable[Transaction],
      damlAppContext: DamlAppContext,
      toplContext: ToplContext
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
}
