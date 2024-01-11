package instances

import zio._
import java.util.UUID

import state.PlayersRepo
import units.Characters

case class InMemoryPlayerStorage(
    storage: Ref[Map[UUID, Characters.CharacterData]]
) extends PlayersRepo {

  def addCharacter(
      name: String,
      id: UUID
  ): UIO[Characters.CharacterData] = {
    val newData = Characters.CharacterData(id, name, 0)
    for {
      _ <- storage.update(_.updated(id, newData))
    } yield newData
  }

  def updateCharacter(
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

  def deleteCharacter(id: UUID): UIO[Option[Characters.CharacterData]] = for {
    mCharacterData <- storage.get.map(_.get(id))
    _ <- storage.update(_.removed(id))
  } yield mCharacterData

  def getAllCharacters(): UIO[List[Characters.CharacterData]] =
    storage.get.map(_.values.toList)

  def getOneCharacter(
      id: UUID
  ): UIO[Option[Characters.CharacterData]] = storage.get.map(_.get(id))

}
