package state

import zio._

import java.util.UUID
import java.sql.SQLException
import units.Characters.CharacterData

trait PlayersRepo {

  def addPlayer(
      name: String,
      id: UUID
  ): ZIO[PlayersRepo, Throwable, CharacterData]
  def updatePlayer(
      id: UUID,
      additionalExp: Int
  ): ZIO[PlayersRepo, Throwable, Option[CharacterData]]
  def deletePlayer(id: UUID): ZIO[PlayersRepo, Throwable, Option[CharacterData]]
  def getAllPlayers(): ZIO[PlayersRepo, Throwable, List[CharacterData]]
  def getOnePlayer(id: UUID): ZIO[PlayersRepo, Throwable, Option[CharacterData]]
}

object PlayersRepo {
  def addPlayer(
      name: String,
      id: UUID
  ): ZIO[PlayersRepo, Throwable, CharacterData] =
    ZIO.serviceWithZIO[PlayersRepo](_.addPlayer(name, id))

  def updatePlayer(
      id: UUID,
      additionalExp: Int
  ): ZIO[PlayersRepo, Throwable, Option[CharacterData]] =
    ZIO.serviceWithZIO[PlayersRepo](_.updatePlayer(id, additionalExp))

  def deletePlayer(
      id: UUID
  ): ZIO[PlayersRepo, Throwable, Option[CharacterData]] =
    ZIO.serviceWithZIO[PlayersRepo](_.deletePlayer(id))

  def getAllPlayers(): ZIO[PlayersRepo, Throwable, List[CharacterData]] =
    ZIO.serviceWithZIO[PlayersRepo](_.getAllPlayers())

  def getOnePlayer(
      id: UUID
  ): ZIO[PlayersRepo, Throwable, Option[CharacterData]] =
    ZIO.serviceWithZIO[PlayersRepo](_.getOnePlayer(id))
}
