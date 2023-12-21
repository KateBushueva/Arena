package app

import zio._
import zio.http._
import zio.json._

import java.util.UUID

import state.PlayersRepo
import units.Players

object PlayerApp {
  def apply(): Http[PlayersRepo, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case Method.GET -> Root / "player" / "allPlayers" =>
        PlayerFlow.getAllPlayers()
      case Method.GET -> Root / "player" / "createPlayer" / name =>
        PlayerFlow.createPlayer(name)
      case Method.GET -> Root / "player" / "getPlayer" / id =>
        PlayerFlow.getPlayer(id)
      case Method.GET -> Root / "player" / "deletePlayer" / id =>
        PlayerFlow.deletePlayer(id)
      // the updatePlayer will be removed from the list of accessible endpoints
      case Method.GET -> root / "player" / "updatePlayer" / id / additionalExp =>
        PlayerFlow.updatePlayer(id, additionalExp.toInt)
    }
}

object PlayerFlow {
  def getAllPlayers(): ZIO[PlayersRepo, Throwable, Response] =
    PlayersRepo
      .getAllPlayers()
      .map(players => Response.json(players.map(_.toJson).toJson))

  def createPlayer(name: String): ZIO[PlayersRepo, Throwable, Response] = for {
    uuid <- Random.nextUUID
    resp <- PlayersRepo
      .addPlayer(name, uuid)
      .map(playerData => Response.json(playerData.toJson))
  } yield resp

  def getPlayer(id: String): ZIO[PlayersRepo, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    PlayersRepo
      .getAllPlayers()
      .map { players =>
        val mPlayer = players.find(player => player.id == uuid)
        Response.json(mPlayer.map(_.toJson).toJson)
      }
  }
  def deletePlayer(id: String): ZIO[PlayersRepo, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    PlayersRepo.deletePlayer(uuid).map(player => Response.json(player.toJson))
  }

  def updatePlayer(
      id: String,
      additionalExp: Int
  ): ZIO[PlayersRepo, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    PlayersRepo
      .updatePlayer(uuid, additionalExp)
      .map(player => Response.json(player.toJson))
  }
}
