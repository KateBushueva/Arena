package instances

import zio._
import java.util.UUID

import state.PlayersRepo
import units.Characters

case class InMemoryPlayerStorage(
    storage: Ref[Map[UUID, Characters.CharacterData]]
) extends PlayersRepo {

  def addPlayer(
      name: String,
      id: UUID
  ): UIO[Characters.CharacterData] = {
    val newData = Characters.CharacterData(id, name, 0)
    for {
      _ <- storage.update(_.updated(id, newData))
    } yield newData
  }

  def updatePlayer(
      id: UUID,
      additionalExp: Int
  ): UIO[Option[Characters.CharacterData]] = for {
    mCharacterData <- storage.get.map(_.get(id))
    mUpdatedData = mCharacterData.map(characterData =>
      Characters.CharacterData(
        id,
        characterData.name,
        characterData.experience + additionalExp
      )
    )
    _ <- mUpdatedData match {
      case Some(updatedData) =>
        storage.update(_.updated(id, updatedData))
      case _ => ZIO.succeed(())
    }
  } yield mUpdatedData

  def deletePlayer(id: UUID): UIO[Option[Characters.CharacterData]] = for {
    mCharacterData <- storage.get.map(_.get(id))
    _ <- storage.update(_.removed(id))
  } yield mCharacterData

  def getAllPlayers(): UIO[List[Characters.CharacterData]] =
    storage.get.map(_.values.toList)

  def getOnePlayer(
      id: UUID
  ): UIO[Option[Characters.CharacterData]] = storage.get.map(_.get(id))

}
