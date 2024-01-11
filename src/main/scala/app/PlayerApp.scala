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
        CharacterFlow.getAllCharacters()
      case Method.GET -> Root / "character" / "newCharacter" / name =>
        CharacterFlow.createCharacter(name)
      case Method.GET -> Root / "character" / "getCharacter" / id =>
        CharacterFlow.getCharacter(id)
      case Method.GET -> Root / "character" / "deleteCharacter" / id =>
        CharacterFlow.deleteCharacter(id)
    }
}

object CharacterFlow {
  def getAllCharacters(): ZIO[PlayersRepo, Throwable, Response] =
    PlayersRepo
      .getAllCharacters()
      .map(players => Response.json(players.map(_.toJson).toJson))

  def createCharacter(name: String): ZIO[PlayersRepo, Throwable, Response] =
    for {
      uuid <- Random.nextUUID
      resp <- PlayersRepo
        .addCharacter(name, uuid)
        .map(characterData => Response.json(characterData.toJson))
    } yield resp

  def getCharacter(id: String): ZIO[PlayersRepo, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    PlayersRepo
      .getOneCharacter(uuid)
      .map {
        case Some(player) =>
          Response.json(player.toJson)
        case None => Response.status(Status.NotFound)
      }
  }
  def deleteCharacter(id: String): ZIO[PlayersRepo, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    PlayersRepo
      .deleteCharacter(uuid)
      .map(player => Response.json(player.toJson))
  }
}
