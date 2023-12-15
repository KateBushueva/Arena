package units

import zio._
import zio.json._

object Players {

  sealed trait Player {
    val name: String
    val hitPoints: Int
    val attack: Int
    def hit(): UIO[Int]
  }

  case class DefaultPlayer(level: Int, givenName: String) extends Player {
    val name = givenName
    val hitPoints: Int = level * 5 + 3
    val attack: Int = level * 3 + 1
    def hit(): UIO[Int] = for {
      damage <- Random.nextIntBetween(1, level * 6)
      result = if (damage == level * 6) damage * 2 else damage
    } yield result
  }

  case class Bot(level: Int) extends Player {
    val name: String = s"BotLvl$level"
    val hitPoints: Int = level * 5 + 3
    val attack: Int = level * 3
    def hit(): UIO[Int] = ZIO.succeed(level * 3)
  }

  implicit val playerEncoder: JsonEncoder[Player] =
    DeriveJsonEncoder.gen[Player]
  implicit val playerDecoder: JsonDecoder[Player] =
    DeriveJsonDecoder.gen[Player]

  object BotLvl1 extends Bot(1)
}
