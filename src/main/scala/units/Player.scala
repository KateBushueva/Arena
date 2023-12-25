package units

import zio._
import zio.json._
import java.util.UUID

object Players {

  sealed trait LevelArgument

  case class Experience(exp: Int) extends LevelArgument
  case class Lev(lev: Int) extends LevelArgument

  case class Level(arg: LevelArgument) {
    val level = arg match {
      case Lev(lev)                      => lev
      case Experience(exp) if exp < 50   => 1
      case Experience(exp) if exp < 150  => 2
      case Experience(exp) if exp < 250  => 3
      case Experience(exp) if exp < 400  => 4
      case Experience(exp) if exp < 600  => 5
      case Experience(exp) if exp < 850  => 6
      case Experience(exp) if exp < 1150 => 7
      case _                             => 8
    }
    val hitPoints: Int = level * 5 + 3
    val attack: Int = level * 6
    val reward: Int = level * 3 + 5
    def hit(): UIO[Int] = for {
      damage <- Random.nextIntBetween(1, attack)
      result = if (damage == attack) damage * 2 else damage
    } yield result
  }

  final case class PlayerData(id: UUID, name: String, experience: Int)

  sealed trait Player {
    val name: String
    val level: Level
    def hit(): UIO[Int]
  }

  case class CustomPlayer(playerData: PlayerData) extends Player {
    val name = playerData.name
    val level = Level(Experience(playerData.experience))
    def hit(): UIO[Int] = level.hit
  }

  sealed case class Bot(lev: Int) extends Player {
    val level = Level(Lev(lev))
    val name: String = s"BotLvl${level.level}"
    def hit(): UIO[Int] = ZIO.succeed(level.attack / 2)
  }

  implicit val playerDataEncoder: JsonEncoder[PlayerData] =
    DeriveJsonEncoder.gen[PlayerData]
  implicit val playerDataDecoder: JsonDecoder[PlayerData] =
    DeriveJsonDecoder.gen[PlayerData]

  implicit val playerEncoder: JsonEncoder[Player] =
    DeriveJsonEncoder.gen[Player]
  implicit val playerDecoder: JsonDecoder[Player] =
    DeriveJsonDecoder.gen[Player]

  object BotLvl1 extends Bot(1)
  object BotLvl2 extends Bot(2)
  object BotLvl3 extends Bot(3)
  object BotLvl4 extends Bot(4)
  object BotLvl5 extends Bot(5)
  object BotLvl6 extends Bot(6)
  object BotLvl7 extends Bot(7)
  object BotLvl8 extends Bot(8)
}
