package units

import zio._
import zio.json._

import java.util.UUID

import Players._

object Game {

  case class BattleState(
      gameId: UUID,
      player1: Player,
      player2: Player,
      damageReceived1: Int,
      damageReceived2: Int
  ) {
    def addDamage(player: Player, damage: Int): BattleState = player match {
      case _ if player == player1 => {
        BattleState(
          gameId,
          player1,
          player2,
          damageReceived1 + damage,
          damageReceived2
        )
      }
      case _ => {
        BattleState(
          gameId,
          player1,
          player2,
          damageReceived1,
          damageReceived2 + damage
        )
      }
    }

    def getWinner(): Option[Player] = (
      player1.hitPoints - damageReceived1,
      player2.hitPoints - damageReceived2
    ) match {
      case (res1, _) if res1 <= 0 => Some(player2)
      case (_, res2) if res2 <= 0 => Some(player1)
      case _                      => None
    }
  }

  object BattleState {
    def apply(gameId: UUID, player1: Player, player2: Player): BattleState =
      new BattleState(gameId, player1, player2, 0, 0)

    implicit val encoder: JsonEncoder[BattleState] =
      DeriveJsonEncoder.gen[BattleState]
    implicit val decoder: JsonDecoder[BattleState] =
      DeriveJsonDecoder.gen[BattleState]
  }
}
