package state

import zio._

import java.util.UUID
import java.sql.SQLException
import units.Players.PlayerData

trait PlayersRepo {

  def addPlayer(name: String, id: UUID): ZIO[PlayersRepo, Throwable, PlayerData]
  def updatePlayer(
      id: UUID,
      additionalExp: Int
  ): ZIO[PlayersRepo, Throwable, PlayerData]
  def deletePlayer(id: UUID): ZIO[PlayersRepo, Throwable, PlayerData]
  def getAllPlayers(): ZIO[PlayersRepo, Throwable, List[PlayerData]]
}

object PlayersRepo {
  def addPlayer(
      name: String,
      id: UUID
  ): ZIO[PlayersRepo, Throwable, PlayerData] =
    ZIO.serviceWithZIO[PlayersRepo](_.addPlayer(name, id))

  def updatePlayer(
      id: UUID,
      additionalExp: Int
  ): ZIO[PlayersRepo, Throwable, PlayerData] =
    ZIO.serviceWithZIO[PlayersRepo](_.updatePlayer(id, additionalExp))

  def deletePlayer(
      id: UUID
  ): ZIO[PlayersRepo, Throwable, PlayerData] =
    ZIO.serviceWithZIO[PlayersRepo](_.deletePlayer(id))

  def getAllPlayers(): ZIO[PlayersRepo, Throwable, List[PlayerData]] =
    ZIO.serviceWithZIO[PlayersRepo](_.getAllPlayers())
}
