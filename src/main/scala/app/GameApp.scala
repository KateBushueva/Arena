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
      case Method.GET -> Root / "allFights" =>
        GameState
          .getFights()
          .map(fights => Response.json(fights.map(_.toJson).toJson))
      case req @ (Method.POST -> Root / "start") =>
        (for {
          name <- req.body.asString.map(_.fromJson[String])
          res <- name match {
            case Left(err) =>
              ZIO
                .debug(s"Failed to parse name: $err")
                .as(Response.text(err).withStatus(Status.BadRequest))
            case Right(givenName) =>
              GameState
                .createFight(givenName)
                .map(fight => Response.text(fight.gameId.toString))
          }
        } yield res)
      case Method.GET -> Root / "getFight" / id =>
        GameState
          .getFightState(UUID.fromString(id))
          .map {
            case Some(fightState) => Response.json(fightState.toJson)
            case None             => Response.status(Status.NotFound)
          }
      case Method.GET -> Root / "hit" / id =>
        (for {
          mFightState <- GameState.getFightState(UUID.fromString(id))
          res <- mFightState match {
            case None => ZIO.succeed(Response.status(Status.NotFound))
            case Some(fightState) =>
              fightState.getWinner match {
                case Some(player) =>
                  ZIO.succeed(
                    Response.text(
                      s"The fight is over, ${player.name} is a winner"
                    )
                  )
                case None =>
                  for {
                    hit1 <- fightState.player1.hit()
                    hit2 <- fightState.player2.hit()
                    updatedState = fightState
                      .addDamage(fightState.player1, hit2)
                      .addDamage(fightState.player2, hit1)
                    updated <- GameState.updateFight(
                      fightState.gameId,
                      updatedState
                    )
                  } yield (Response.json(updated.toJson))
              }
          }
        } yield (res))
      case Method.GET -> Root / "delete" / id =>
        GameState
          .removeFight(UUID.fromString(id))
          .map {
            case Some(fightState) => Response.json(fightState.toJson)
            case None             => Response.status(Status.NotFound)
          }
    }
}
