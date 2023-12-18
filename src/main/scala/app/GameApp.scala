package app

import zio._
import zio.http._
import zio.json._

import java.util.UUID

import state.GameState
import units.Game.FightState._

object GameApp {
  def apply(): Http[GameState, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case Method.GET -> Root / "allFights"     => GameFlow.getAllFights()
      case Method.GET -> Root / "start" / name  => GameFlow.startNewFight(name)
      case Method.GET -> Root / "getFight" / id => GameFlow.getFight(id)
      case Method.GET -> Root / "hit" / id      => GameFlow.hit(id)
      case Method.GET -> Root / "delete" / id   => GameFlow.deleteFight(id)
    }
}

object GameFlow {
  def getAllFights(): ZIO[GameState, Throwable, Response] =
    GameState
      .getFights()
      .map(fights => Response.json(fights.map(_.toJson).toJson))

  def startNewFight(name: String): ZIO[GameState, Throwable, Response] =
    GameState
      .createFight(name)
      .map(fight => Response.text(fight.gameId.toString))

  def getFight(id: String): ZIO[GameState, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    GameState
      .getFightState(UUID.fromString(id))
      .map {
        case Some(fightState) => Response.json(fightState.toJson)
        case None             => Response.status(Status.NotFound)
      }
  }

  def deleteFight(id: String): ZIO[GameState, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    GameState
      .getFightState(uuid)
      .flatMap {
        case None => ZIO.succeed(Response.status(Status.NotFound))
        case Some(_) =>
          GameState
            .removeFight(uuid)
            .map(_ => Response.text(s"$id fight deleted successfully"))
      }
  }

  def hit(id: String): ZIO[GameState, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    GameState
      .getFightState(uuid)
      .flatMap {
        case None => ZIO.succeed(Response.status(Status.NotFound))
        case Some(fightState) =>
          fightState.getWinner() match {
            case Some(player) =>
              ZIO.succeed(
                Response.text(s"The fight is over, ${player.name} won")
              )
            case _ =>
              for {
                hit1 <- fightState.player1.hit()
                hit2 <- fightState.player2.hit()
                updatedState = fightState
                  .addDamage(fightState.player1, hit2)
                  .addDamage(fightState.player2, hit1)
                response <- GameState
                  .updateFight(
                    fightState.gameId,
                    updatedState
                  )
                  .map(updated => Response.json(updated.toJson))
              } yield response
          }
      }
  }
}
