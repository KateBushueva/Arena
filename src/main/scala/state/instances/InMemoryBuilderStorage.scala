package state.instances

import zio._

import java.util.UUID

import state.P2pBuilderState
import units.Game._
import units.Game
import units.Characters

case class InMemoryBattleBuilderState(storage: Ref[Map[UUID, BattleBuilder]])
    extends P2pBuilderState {

  def createP2p(creator: Characters.Character): UIO[Game.BattleBuilder] = for {
    id <- Random.nextUUID
    builder = BattleBuilder(id, creator)
    _ <- storage.update(_.updated(id, builder))
  } yield builder

  def getP2p(id: UUID): UIO[Option[Game.BattleBuilder]] =
    storage.get.map(_.get(id))

  def removeFromStorage(id: UUID): UIO[Unit] = for {
    _ <- storage.update(_.removed(id))
  } yield ()
  def getAllP2ps(): Task[List[Game.BattleBuilder]] =
    storage.get.map(_.values.toList)
}

object InMemoryBattleBuilderState {
  def layer: ZLayer[Any, Nothing, InMemoryBattleBuilderState] =
    ZLayer.fromZIO(
      Ref
        .make(Map.empty[UUID, BattleBuilder])
        .map(new InMemoryBattleBuilderState(_))
    )
}
