package units

import zio._
import zio.json._

import java.util.UUID

import Players._

object Game {
  
  case class FightState(gameId: UUID, player1: Player, player2: Player, damageReceived1: Int, damageReceived2: Int) {
    def addDamage(player:Player, damage: Int):FightState = player match {
      case _ if player == player1 => new FightState(gameId,player1,player2,damageReceived1+damage,damageReceived2)
      case _ => new FightState(gameId,player1,player2,damageReceived1,damageReceived2+damage)
    }

    def getWinner():Option[Player] = (player1.hitPoints-damageReceived1, player2.hitPoints-damageReceived2) match {
      case (res1,_) if res1 <= 0 => Some(player2)
      case (_,res2) if res2 <= 0 => Some(player1)
      case _ => None
    }
  }

  object FightState {
   def apply(gameId: UUID, player1: Player, player2: Player): FightState = new FightState(gameId, player1, player2, 0, 0)

   implicit val encoder: JsonEncoder[FightState] = DeriveJsonEncoder.gen[FightState]
   implicit val decoder: JsonDecoder[FightState] = DeriveJsonDecoder.gen[FightState]

  }


  case class GameFlow() {

    def startNewFight(player1:Player, player2:Player): UIO[FightState] = for {
      id <- Random.nextUUID
    } yield FightState(id, player1,player2)

    def hit(player: Player, fightState:FightState): UIO[FightState] = for {
      damage <- player.hit()
    } yield fightState.addDamage(player,damage)
  }

}

