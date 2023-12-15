package app

import zio._
import zio.http._
import zio.json._
import state.GameState
import units.Game.FightState._

object GameApp {
  def apply(): Http[GameState, Throwable,Request, Response] = 
    Http.collectZIO[Request] {
      case req @ (Method.POST -> Root / "start") => 
        (for {
          name <- req.body.asString.map(_.fromJson[String])
          r <- name match {
            case Left(err) => ZIO.debug(s"Failed to parse name: $err").as(Response.text(err).withStatus(Status.BadRequest))
            case Right(givenName) => GameState.createFight(givenName).map(fight => Response.text(fight.gameId.toString))
          }
        } yield r)
    }
}
