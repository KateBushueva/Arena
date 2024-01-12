package units

import zio._
import zio.json._

import java.util.UUID

import Characters._

object Game {

  case class BattleState(
      gameId: UUID,
      combatant1: Character,
      combatant2: Character,
      damageReceived1: Int,
      damageReceived2: Int
  ) {
    def addDamage(player: Character, damage: Int): BattleState = player match {
      case _ if player == combatant1 =>
        BattleState(
          gameId,
          combatant1,
          combatant2,
          damageReceived1 + damage,
          damageReceived2
        )
      case _ =>
        BattleState(
          gameId,
          combatant1,
          combatant2,
          damageReceived1,
          damageReceived2 + damage
        )
    }

    def getWinner(): Option[Character] = (
      combatant1.level.hitPoints - damageReceived1,
      combatant2.level.hitPoints - damageReceived2
    ) match {
      case (res1, res2) if res1 <= 0 || res2 <= 0 =>
        if (res1 < res2) Some(combatant2) else Some(combatant1)
      case _ => None
    }

    def experienceReceived(): Int = {
      getWinner() match {
        case None                                 => 0
        case Some(player) if player == combatant1 => combatant2.level.reward
        case _                                    => combatant1.level.reward
      }
    }
  }

  object BattleState {
    def apply(
        gameId: UUID,
        player1: Character,
        player2: Character
    ): BattleState =
      new BattleState(gameId, player1, player2, 0, 0)

    implicit val battleStateEncoder: JsonEncoder[BattleState] =
      DeriveJsonEncoder.gen[BattleState]
    implicit val battleStateDecoder: JsonDecoder[BattleState] =
      DeriveJsonDecoder.gen[BattleState]
  }

  final case class BattleBuilder(builderId: UUID, creator: Character)

  object BattleBuilder {
    implicit val battleBuilderEncoder: JsonEncoder[BattleBuilder] =
      DeriveJsonEncoder.gen[BattleBuilder]
    implicit val battleBuilderDecoder: JsonDecoder[BattleBuilder] =
      DeriveJsonDecoder.gen[BattleBuilder]
  }
}
