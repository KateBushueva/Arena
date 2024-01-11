package state.instances

import io.getquill._
import io.getquill.jdbczio.Quill
// import io.getquill.util.LoadConfig
import zio._

import java.sql.SQLException
import java.util.UUID
import javax.sql.DataSource

import state.PlayersRepo
import units.Characters.CharacterData

final case class PersistentPlayersRepo(src: DataSource) extends PlayersRepo {

  val ctx = new PostgresZioJdbcContext(SnakeCase)
  import ctx._

  def addCharacter(
      name: String,
      uuid: UUID
  ): ZIO[PlayersRepo, SQLException, CharacterData] = {
    val characterData = CharacterData(uuid, name, 0)
    run {
      query[CharacterData]
        .insertValue(lift(characterData))
        .returning(a => a)
    }.provide(ZLayer.succeed(src))
  }

  def updateCharacter(
      id: UUID,
      additionalExp: Int
  ): ZIO[PlayersRepo, SQLException, Option[CharacterData]] =
    run {
      query[CharacterData]
        .filter(p => p.id == lift(id))
        .update(player =>
          player.experience -> (player.experience + lift(additionalExp))
        )
        .returningMany(a => a)
    }.map(_.headOption).provide(ZLayer.succeed(src))

  def deleteCharacter(
      id: UUID
  ): ZIO[PlayersRepo, SQLException, Option[CharacterData]] =
    run {
      query[CharacterData]
        .filter(p => p.id == lift(id))
        .delete
        .returningMany(a => a)
    }.map(_.headOption).provide(ZLayer.succeed(src))

  def getAllCharacters(): ZIO[PlayersRepo, SQLException, List[CharacterData]] =
    run {
      query[CharacterData]
    }
      .provide(ZLayer.succeed(src))

  def getOneCharacter(
      id: UUID
  ): ZIO[PlayersRepo, SQLException, Option[CharacterData]] =
    run {
      query[CharacterData].filter(p => p.id == lift(id))
    }
      .map(_.headOption)
      .provide(ZLayer.succeed(src))
}

object PersistentPlayersRepoLayer {
  def layer = ZLayer.fromFunction(PersistentPlayersRepo.apply _)
}
