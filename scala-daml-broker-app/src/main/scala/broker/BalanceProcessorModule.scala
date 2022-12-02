package broker

import io.reactivex.Flowable
import co.topl.daml.DamlAppContext
import co.topl.daml.ToplContext
import com.daml.ledger.javaapi.data.Transaction
import cats.effect.IO
import co.topl.daml.assets.processors.AssetTransferRequestProcessor
import co.topl.daml.assets.processors.SignedAssetTransferRequestProcessor
import co.topl.daml.assets.processors.AssetTransferRequestEd25519Processor
import co.topl.daml.api.model.topl.asset.AssetBalanceRequest
import co.topl.daml.assets.processors.AssetBalanceRequestProcessor

trait BalanceProcessorModule {

  def registerAssetBalanceProcessor()(implicit
      transactions: Flowable[Transaction],
      damlAppContext: DamlAppContext,
      toplContext: ToplContext
  ) =
    for {
      transferProcessor <- IO(
        new AssetBalanceRequestProcessor(
          damlAppContext,
          toplContext,
          3000,
          (x, y) => true,
          t => true
        )
      )
      _ <- IO(transactions.forEach(transferProcessor.processTransaction))
    } yield ()

}
