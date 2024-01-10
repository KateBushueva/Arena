package app

import zio._
import zio.http._
import zio.json._

import java.util.UUID

import state.PlayersRepo
import units.Characters

object CharacterApp {
  def apply(): Http[PlayersRepo, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case Method.GET -> Root / "character" / "allCharacters" =>
        CharacterFlow.getAllPlayers()
      case Method.GET -> Root / "character" / "newCharacter" / name =>
        CharacterFlow.createPlayer(name)
      case Method.GET -> Root / "character" / "getCharacter" / id =>
        CharacterFlow.getPlayer(id)
      case Method.GET -> Root / "character" / "deleteCharacter" / id =>
        CharacterFlow.deletePlayer(id)
    }
}

object CharacterFlow {
  def getAllPlayers(): ZIO[PlayersRepo, Throwable, Response] =
    PlayersRepo
      .getAllPlayers()
      .map(players => Response.json(players.map(_.toJson).toJson))

  def createPlayer(name: String): ZIO[PlayersRepo, Throwable, Response] = for {
    uuid <- Random.nextUUID
    resp <- PlayersRepo
      .addPlayer(name, uuid)
      .map(characterData => Response.json(characterData.toJson))
  } yield resp

  def getPlayer(id: String): ZIO[PlayersRepo, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    PlayersRepo
      .getOnePlayer(uuid)
      .map {
        case Some(player) =>
          Response.json(player.toJson)
        case None => Response.status(Status.NotFound)
      }
  }
  def deletePlayer(id: String): ZIO[PlayersRepo, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    PlayersRepo.deletePlayer(uuid).map(player => Response.json(player.toJson))
  }
}
