package state

import zio._

import java.util.UUID

import units.Game
import units.Characters

trait GameState {
  def createBattle(
      combatant1: Characters.Character,
      combatant2: Characters.Character
  ): Task[Game.BattleState]
  def updateBattle(
      id: UUID,
      newBattle: Game.BattleState
  ): Task[Game.BattleState]
  def removeBattle(id: UUID): Task[Unit]
  def getBattleState(id: UUID): Task[Option[Game.BattleState]]
  def getBattles(): Task[List[Game.BattleState]]
}

object GameState {
  def createBattle(
      combatant1: Characters.Character,
      combatant2: Characters.Character
  ): ZIO[GameState, Throwable, Game.BattleState] =
    ZIO.serviceWithZIO[GameState](_.createBattle(combatant1, combatant2))

  def updateBattle(
      id: UUID,
      newBattle: Game.BattleState
  ): ZIO[GameState, Throwable, Game.BattleState] =
    ZIO.serviceWithZIO[GameState](_.updateBattle(id, newBattle))

  def removeBattle(
      id: UUID
  ): ZIO[GameState, Throwable, Unit] =
    ZIO.serviceWithZIO[GameState](_.removeBattle(id))

  def getBattleState(
      id: UUID
  ): ZIO[GameState, Throwable, Option[Game.BattleState]] =
    ZIO.serviceWithZIO[GameState](_.getBattleState(id))

  def getBattles(): ZIO[GameState, Throwable, List[Game.BattleState]] =
    ZIO.serviceWithZIO[GameState](_.getBattles())
}
