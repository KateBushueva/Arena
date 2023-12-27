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
  ): UIO[Option[Players.PlayerData]] = for {
    mPlayerData <- storage.get.map(_.get(id))
    mUpdatedData = mPlayerData.map(playerData =>
      Players.PlayerData(
        id,
        playerData.name,
        playerData.experience + additionalExp
      )
    )
    _ <- mUpdatedData match {
      case Some(updatedData) =>
        storage.update(_.updated(id, updatedData))
      case _ => ZIO.succeed(())
    }
  } yield mUpdatedData

  def deletePlayer(id: UUID): UIO[Option[Players.PlayerData]] = for {
    mPlayerData <- storage.get.map(_.get(id))
    _ <- storage.update(_.removed(id))
  } yield mPlayerData

  def getAllPlayers(): UIO[List[Players.PlayerData]] =
    storage.get.map(_.values.toList)

  def getOnePlayer(
      id: UUID
  ): UIO[Option[Players.PlayerData]] = storage.get.map(_.get(id))

}
