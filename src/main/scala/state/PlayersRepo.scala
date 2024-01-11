package state

import zio._

import java.util.UUID
import java.sql.SQLException
import units.Characters.CharacterData

trait PlayersRepo {

  def addCharacter(
      name: String,
      id: UUID
  ): ZIO[PlayersRepo, Throwable, CharacterData]
  def updateCharacter(
      id: UUID,
      additionalExp: Int
  ): ZIO[PlayersRepo, Throwable, Option[CharacterData]]
  def deleteCharacter(
      id: UUID
  ): ZIO[PlayersRepo, Throwable, Option[CharacterData]]
  def getAllCharacters(): ZIO[PlayersRepo, Throwable, List[CharacterData]]
  def getOneCharacter(
      id: UUID
  ): ZIO[PlayersRepo, Throwable, Option[CharacterData]]
}

object PlayersRepo {
  def addCharacter(
      name: String,
      id: UUID
  ): ZIO[PlayersRepo, Throwable, CharacterData] =
    ZIO.serviceWithZIO[PlayersRepo](_.addCharacter(name, id))

  def updateCharacter(
      id: UUID,
      additionalExp: Int
  ): ZIO[PlayersRepo, Throwable, Option[CharacterData]] =
    ZIO.serviceWithZIO[PlayersRepo](_.updateCharacter(id, additionalExp))

  def deleteCharacter(
      id: UUID
  ): ZIO[PlayersRepo, Throwable, Option[CharacterData]] =
    ZIO.serviceWithZIO[PlayersRepo](_.deleteCharacter(id))

  def getAllCharacters(): ZIO[PlayersRepo, Throwable, List[CharacterData]] =
    ZIO.serviceWithZIO[PlayersRepo](_.getAllCharacters())

  def getOneCharacter(
      id: UUID
  ): ZIO[PlayersRepo, Throwable, Option[CharacterData]] =
    ZIO.serviceWithZIO[PlayersRepo](_.getOneCharacter(id))
}
