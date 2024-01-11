package state.instances

import zio._

import java.util.UUID

import state.GameState
import units.Game._
import units.Game
import units.Characters

case class InMemoryGameState(storage: Ref[Map[UUID, BattleState]])
    extends GameState {
  def createBattle(
      combatant1: Characters.Character,
      combatant2: Characters.Character
  ): UIO[Game.BattleState] = for {
    gameId <- Random.nextUUID
    newBattle = BattleState(
      gameId,
      combatant1,
      combatant2
    )
    _ <- storage.update(_.updated(gameId, newBattle))
  } yield newBattle

  def updateBattle(
      id: UUID,
      updatedBattle: Game.BattleState
  ): UIO[Game.BattleState] = for {
    _ <- storage.update(_.updated(id, updatedBattle))
  } yield updatedBattle

  def removeBattle(id: UUID): UIO[Unit] = for {
    _ <- storage.update(_.removed(id))
  } yield ()

  def getBattleState(id: UUID): UIO[Option[Game.BattleState]] =
    storage.get.map(_.get(id))

  def getBattles(): UIO[List[Game.BattleState]] =
    storage.get.map(_.values.toList)
}

object InMemoryGameState {
  def layer: ZLayer[Any, Nothing, InMemoryGameState] =
    ZLayer.fromZIO(
      Ref
        .make(Map.empty[UUID, BattleState])
        .map(new InMemoryGameState(_))
    )
}
