package units

import zio._
import Players._
import java.util.UUID

object Game {
  
  class FightState(val gameId: UUID, player1: Player, player2: Player, val damageReceived1: Int, val damageReceived2: Int) {
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
  }

/**
  * object User {
  implicit val encoder: JsonEncoder[User] =
    DeriveJsonEncoder.gen[User]
  implicit val decoder: JsonDecoder[User] =
    DeriveJsonDecoder.gen[User]
}
  */


  case class GameFlow() {

    def startNewFight(player1:Player, player2:Player): UIO[FightState] = for {
      id <- Random.nextUUID
    } yield FightState(id, player1,player2)

    def hit(player: Player, fightState:FightState): UIO[FightState] = for {
      damage <- player.hit()
    } yield fightState.addDamage(player,damage)
  }

}

