package state

import zio._
import scala.collection.mutable
import units.Game._
import units.Game
import units.Players
import java.util.UUID

case class InMemoryGameState(storage:Ref[mutable.Map[UUID,FightState]]) extends GameState {
  // For now creates a fight with BotLvl1
  def createFight(playerName: String): UIO[Game.FightState] = for {
    gameId <- Random.nextUUID
    newFight = FightState(gameId, Players.DefaultPlayer(1,playerName), Players.BotLvl1)
    _ <- storage.updateAndGet(_.addOne(gameId,newFight))
  } yield newFight

  def updateFight(id:UUID, updatedFight:Game.FightState):UIO[Option[Game.FightState]] = for {
    mFight <- storage.get.map(_.get(id))
    result = mFight match {
        case None => None
        case Some(_) => 
          storage.updateAndGet(_.subtractOne(id).addOne(id,updatedFight))
          Some(updatedFight)
      }
  } yield result

  def removeFight(id:UUID):UIO[Option[Game.FightState]] = for {
    mFight <- storage.get.map(_.get(id))
    result = mFight match {
      case None => None
      case Some(fight) => 
        storage.updateAndGet(_.subtractOne(id))
        Some(fight)
    } 
  } yield(result)

  def getFightState(id:UUID):UIO[Option[Game.FightState]] = 
    storage.get.map(_.get(id))
}

object InMemoryGameState {
  def layer: ZLayer[Any, Nothing, InMemoryGameState] =
    ZLayer.fromZIO(
      Ref.make(mutable.Map.empty[UUID,FightState]).map(new InMemoryGameState(_))
    )
}