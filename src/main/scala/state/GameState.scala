package state

import zio._

import java.util.UUID

import units.Game

trait GameState {
  def createFight(playerName: String): Task[Game.FightState]
  def updateFight(
      id: UUID,
      newFight: Game.FightState
  ): Task[Game.FightState]
  def removeFight(id: UUID): Task[Unit]
  def getFightState(id: UUID): Task[Option[Game.FightState]]
  def getFights(): Task[List[Game.FightState]]
}

object GameState {
  def createFight(
      playerName: String
  ): ZIO[GameState, Throwable, Game.FightState] =
    ZIO.serviceWithZIO[GameState](_.createFight(playerName))

  def updateFight(
      id: UUID,
      newFight: Game.FightState
  ): ZIO[GameState, Throwable, Game.FightState] =
    ZIO.serviceWithZIO[GameState](_.updateFight(id, newFight))

  def removeFight(
      id: UUID
  ): ZIO[GameState, Throwable, Unit] =
    ZIO.serviceWithZIO[GameState](_.removeFight(id))

  def getFightState(
      id: UUID
  ): ZIO[GameState, Throwable, Option[Game.FightState]] =
    ZIO.serviceWithZIO[GameState](_.getFightState(id))

  def getFights(): ZIO[GameState, Throwable, List[Game.FightState]] =
    ZIO.serviceWithZIO[GameState](_.getFights())
}
