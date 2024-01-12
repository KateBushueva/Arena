package app

import zio._
import zio.http._
import zio.json._

import java.util.UUID

import state.GameState
import state.P2pBuilderState
import state.PlayersRepo
import units.Game.BattleState._
import units.Characters
import units.Characters.Bot
import units.Characters.CustomCharacter

object GameApp {
  def apply(): Http[
    GameState with PlayersRepo with P2pBuilderState,
    Throwable,
    Request,
    Response
  ] =
    // TODO: replace GET requests with POST requests with corresponding request bodies
    Http.collectZIO[Request] {
      case Method.GET -> Root / "allBattles" => GameFlow.getAllBattles()
      case Method.GET -> Root / "allP2ps"    => GameFlow.getAllP2ps()
      case Method.GET -> Root / "startWithBot" / characterId =>
        GameFlow.startWithBot(characterId)
      case Method.GET -> Root / "createBattle" / characterId =>
        GameFlow.createBattle(characterId)
      case Method.GET -> Root / "getBattle" / battleId =>
        GameFlow.getBattle(battleId)
      case Method.GET -> Root / "hit" / battleId => GameFlow.hit(battleId)
      case Method.GET -> Root / "completeBattle" / battleId =>
        GameFlow.completeBattle(battleId)
    }
}

object GameFlow {
  def getAllBattles(): ZIO[GameState, Throwable, Response] =
    GameState
      .getBattles()
      .map(battles => Response.json(battles.map(_.toJson).toJson))

  def getAllP2ps(): ZIO[P2pBuilderState, Throwable, Response] =
    P2pBuilderState
      .getAllP2ps()
      .map(p2ps => Response.json(p2ps.map(_.toJson).toJson))

  def startWithBot(
      id: String
  ): ZIO[GameState with PlayersRepo, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    for {
      resp <- PlayersRepo.getOneCharacter(uuid).flatMap {
        case Some(characterData) => {
          val character = Characters.CustomCharacter(characterData)
          val bot = Characters.selectBot(character.level.level)
          GameState
            .createBattle(character, bot)
            .map(battle => Response.text(battle.gameId.toString))
        }
        case None => ZIO.succeed(Response.status(Status.NotFound))
      }
    } yield resp
  }

  def createBattle(
      id: String
  ): ZIO[P2pBuilderState with PlayersRepo, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    for {
      resp <- PlayersRepo.getOneCharacter(uuid).flatMap {
        case Some(characterData) => {
          val creator = Characters.CustomCharacter(characterData)
          P2pBuilderState
            .createP2p(creator)
            .map(p2p => Response.text(p2p.builderId.toString))
        }
        case None => ZIO.succeed(Response.status(Status.NotFound))
      }
    } yield resp
  }

  def getBattle(id: String): ZIO[GameState, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    GameState
      .getBattleState(UUID.fromString(id))
      .map {
        case Some(battleState) => Response.json(battleState.toJson)
        case None              => Response.status(Status.NotFound)
      }
  }

  def completeBattle(
      id: String
  ): ZIO[GameState with PlayersRepo, Throwable, Response] = {
    val uuid = UUID.fromString(id)
    GameState
      .getBattleState(uuid)
      .flatMap {
        case None => ZIO.succeed(Response.status(Status.NotFound))
        case Some(battleState) =>
          battleState.getWinner() match {
            case None =>
              GameState
                .removeBattle(uuid)
                .map(_ => Response.text(s"$id battle removed successfully"))
            case Some(player) =>
              player match {
                case Bot(_) =>
                  GameState
                    .removeBattle(uuid)
                    .map(_ =>
                      Response.text(s"The battle is over, ${player.name} won")
                    )
                case CustomCharacter(data) =>
                  for {
                    mUpdated <- PlayersRepo.updateCharacter(
                      data.id,
                      battleState.experienceReceived
                    )
                    resp <- mUpdated match {
                      case Some(updated) =>
                        GameState
                          .removeBattle(uuid)
                          .map(_ =>
                            Response.text(
                              s"The battle is over, ${updated.name} won. ${battleState.experienceReceived} experience received"
                            )
                          )
                      case _ =>
                        ZIO.succeed(Response.status(Status.InternalServerError))
                    }

                  } yield resp
              }
          }
      }
  }
  // TODO: fix the endpoint to get hit requests from different users
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
                hit1 <- battleState.combatant1.hit()
                hit2 <- battleState.combatant2.hit()
                updatedState = battleState
                  .addDamage(battleState.combatant1, hit2)
                  .addDamage(battleState.combatant2, hit1)
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
