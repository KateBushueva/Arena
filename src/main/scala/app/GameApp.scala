package app

import zio._
import zio.http._
import zio.json._

import java.util.UUID

import state.GameState
import units.Game.BattleState._

object GameApp {
  def apply(): Http[GameState, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case Method.GET -> Root / "allBattles"   => GameFlow.getAllBattles()
      case Method.GET -> Root / "start" / name => GameFlow.startNewBattle(name)
      case Method.GET -> Root / "getBattle" / id => GameFlow.getBattle(id)
      case Method.GET -> Root / "hit" / id       => GameFlow.hit(id)
      case Method.GET -> Root / "delete" / id    => GameFlow.deleteBattle(id)
    }
}

object GameFlow {
  def getAllBattles(): ZIO[GameState, Throwable, Response] =
    GameState
      .getBattles()
      .map(battles => Response.json(battles.map(_.toJson).toJson))

  def startNewBattle(name: String): ZIO[GameState, Throwable, Response] =
    GameState
      .createBattle(name)
      .map(battle => Response.text(battle.gameId.toString))

  def getBattle(id: String): ZIO[GameState, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    GameState
      .getBattleState(UUID.fromString(id))
      .map {
        case Some(battleState) => Response.json(battleState.toJson)
        case None              => Response.status(Status.NotFound)
      }
  }

  def deleteBattle(id: String): ZIO[GameState, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    GameState
      .getBattleState(uuid)
      .flatMap {
        case None => ZIO.succeed(Response.status(Status.NotFound))
        case Some(_) =>
          GameState
            .removeBattle(uuid)
            .map(_ => Response.text(s"$id battle deleted successfully"))
      }
  }

  def hit(id: String): ZIO[GameState, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    GameState
      .getBattleState(uuid)
      .flatMap {
        case None => ZIO.succeed(Response.status(Status.NotFound))
        case Some(battleState) =>
          battleState.getWinner() match {
            case Some(player) =>
              ZIO.succeed(
                Response.text(s"The battle is over, ${player.name} won")
              )
            case _ =>
              for {
                hit1 <- battleState.player1.hit()
                hit2 <- battleState.player2.hit()
                updatedState = battleState
                  .addDamage(battleState.player1, hit2)
                  .addDamage(battleState.player2, hit1)
                response <- GameState
                  .updateBattle(
                    battleState.gameId,
                    updatedState
                  )
                  .map(updated => Response.json(updated.toJson))
              } yield response
          }
      }
  }
}
