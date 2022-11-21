package broker

import io.reactivex.Flowable
import co.topl.daml.DamlAppContext
import co.topl.daml.ToplContext
import com.daml.ledger.javaapi.data.Transaction
import cats.effect.IO
import co.topl.daml.assets.processors.AssetTransferRequestProcessor
import co.topl.daml.assets.processors.SignedAssetTransferRequestProcessor

trait AssetTransferProcessorRegistrationModule {

  def registerAssetTransferRequestProcessor()(implicit
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

  def registerSignedAssetTransferRequestProcessor()(implicit
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
}
