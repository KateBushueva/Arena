package state

import zio._

import java.util.UUID

import units.Game
import units.Characters

trait P2pBuilderState {
  def createP2p(creator: Characters.Character): Task[Game.BattleBuilder]
  def getP2p(id: UUID): Task[Option[Game.BattleBuilder]]
  def removeFromStorage(id: UUID): Task[Unit]
  def getAllP2ps(): Task[List[Game.BattleBuilder]]
}

object P2pBuilderState {
  def createP2p(
      creator: Characters.Character
  ): ZIO[P2pBuilderState, Throwable, Game.BattleBuilder] =
    ZIO.serviceWithZIO[P2pBuilderState](_.createP2p(creator))

  def getP2p(
      id: UUID
  ): ZIO[P2pBuilderState, Throwable, Option[Game.BattleBuilder]] =
    ZIO.serviceWithZIO[P2pBuilderState](_.getP2p(id))

  def removeFromStorage(
      id: UUID
  ): ZIO[P2pBuilderState, Throwable, Unit] =
    ZIO.serviceWithZIO[P2pBuilderState](_.removeFromStorage(id))

  def getAllP2ps(): ZIO[P2pBuilderState, Throwable, List[Game.BattleBuilder]] =
    ZIO.serviceWithZIO[P2pBuilderState](_.getAllP2ps())
}
