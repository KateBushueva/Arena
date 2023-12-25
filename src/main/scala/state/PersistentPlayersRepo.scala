package state

import io.getquill._
import io.getquill.jdbczio.Quill
import zio._

import java.sql.SQLException
import java.util.UUID
import javax.sql.DataSource

import units.Players.PlayerData
import io.getquill.util.LoadConfig

final case class PersistentPlayersRepo(src: DataSource) extends PlayersRepo {

  val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  def addPlayer(
      name: String,
      uuid: UUID
  ): ZIO[PlayersRepo, SQLException, PlayerData] = {
    val playerData = PlayerData(uuid, name, 0)
    run {
      query[PlayerData]
        .insertValue(lift(playerData))
        .returning(a => a)
    }.provide(ZLayer.succeed(src))
  }

  def updatePlayer(
      id: UUID,
      additionalExp: Int
  ): ZIO[PlayersRepo, SQLException, PlayerData] =
    run {
      query[PlayerData]
        .filter(p => p.id == lift(id))
        .update(player =>
          player.experience -> (player.experience + lift(additionalExp))
        )
        .returning(a => a)
    }.provide(ZLayer.succeed(src))

  def deletePlayer(
      id: UUID
  ): ZIO[PlayersRepo, SQLException, PlayerData] =
    run {
      query[PlayerData].filter(p => p.id == lift(id)).delete.returning(a => a)
    }.provide(ZLayer.succeed(src))

  def getAllPlayers(): ZIO[PlayersRepo, SQLException, List[PlayerData]] =
    run {
      query[PlayerData]
    }
      .provide(ZLayer.succeed(src))

  def getOnePlayer(
      id: UUID
  ): ZIO[PlayersRepo, SQLException, Option[PlayerData]] =
    run {
      query[PlayerData].filter(p => p.id == lift(id))
    }
      .map(_.headOption)
      .provide(ZLayer.succeed(src))
}

object PersistentPlayersRepoLayer {
  def layer = ZLayer.fromFunction(PersistentPlayersRepo.apply _)
}
