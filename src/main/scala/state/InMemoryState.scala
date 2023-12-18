package state

import zio._

import java.util.UUID

import units.Game._
import units.Game
import units.Players

case class InMemoryGameState(storage: Ref[Map[UUID, FightState]])
    extends GameState {
  // For now creates a fight with BotLvl1
  def createFight(playerName: String): UIO[Game.FightState] = for {
    gameId <- Random.nextUUID
    newFight = FightState(
      gameId,
      Players.DefaultPlayer(1, playerName),
      Players.BotLvl1
    )
    _ <- storage.update(_.updated(gameId, newFight))
  } yield newFight

  def updateFight(
      id: UUID,
      updatedFight: Game.FightState
  ): UIO[Game.FightState] = for {
    _ <- storage.update(_.updated(id, updatedFight))
  } yield updatedFight

  def removeFight(id: UUID): UIO[Unit] = for {
    _ <- storage.update(_.removed(id))
  } yield ()

  def getFightState(id: UUID): UIO[Option[Game.FightState]] =
    storage.get.map(_.get(id))

  def getFights(): UIO[List[Game.FightState]] =
    storage.get.map(_.values.toList)
}

object InMemoryGameState {
  def layer: ZLayer[Any, Nothing, InMemoryGameState] =
    ZLayer.fromZIO(
      Ref
        .make(Map.empty[UUID, FightState])
        .map(new InMemoryGameState(_))
    )
}
