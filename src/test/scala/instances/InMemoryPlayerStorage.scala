package instances

import zio._
import java.util.UUID

import state.PlayersRepo
import units.Players

case class InMemoryPlayerStorage(storage: Ref[Map[UUID, Players.PlayerData]])
    extends PlayersRepo {

  def addPlayer(
      name: String,
      id: UUID
  ): UIO[Players.PlayerData] = {
    val newData = Players.PlayerData(id, name, 0)
    for {
      _ <- storage.update(_.updated(id, newData))
    } yield newData
  }

  def updatePlayer(
      id: UUID,
      additionalExp: Int
  ): UIO[Players.PlayerData] = for {
    playerData <- storage.get.map(_.apply(id))
    updatedData = Players.PlayerData(
      id,
      playerData.name,
      playerData.experience + additionalExp
    )
    _ <- storage.update(_.updated(id, updatedData))
  } yield updatedData

  def deletePlayer(id: UUID): UIO[Players.PlayerData] = for {
    playerData <- storage.get.map(_.apply(id))
    _ <- storage.update(_.removed(id))
  } yield playerData

  def getAllPlayers(): UIO[List[Players.PlayerData]] =
    storage.get.map(_.values.toList)

  def getOnePlayer(
      id: UUID
  ): UIO[Option[Players.PlayerData]] = storage.get.map(_.get(id))

}
