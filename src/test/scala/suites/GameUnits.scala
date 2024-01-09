package suites

import zio.test._
import zio.test.Assertion._

import java.util.UUID

import units.Players

object GameUnitTests {
  val uuid = UUID.fromString("b2c8ccb8-191a-4233-9b34-3e3111a4adaf")

  val playerSecondLvl =
    Players.CustomPlayer(Players.PlayerData(uuid, "Melody", 55))

  val playerHighLvl =
    Players.CustomPlayer(Players.PlayerData(uuid, "Pumpkin Jack", 1500))

  val playerCreation =
    test("Should create a player with expected fields")(
      assert(playerSecondLvl)(
        hasField(
          "name",
          (p: Players.CustomPlayer) => p.name,
          equalTo("Melody")
        ) &&
          hasField(
            "level",
            (p: Players.CustomPlayer) => p.level.level,
            equalTo(2)
          ) &&
          hasField(
            "attack",
            (p: Players.CustomPlayer) => p.level.attack,
            equalTo(12)
          ) &&
          hasField(
            "hitPoints",
            (p: Players.CustomPlayer) => p.level.hitPoints,
            equalTo(13)
          ) &&
          hasField(
            "reward",
            (p: Players.CustomPlayer) => p.level.reward,
            equalTo(11)
          )
      )
    )

  val botSelection =
    suite("Should select the bot of correct level")(
      test("Should select BotLvl2")(
        assertTrue(
          Players.selectBot(playerSecondLvl.level.level) == Players.BotLvl2
        )
      ),
      test("Should select BotLvl8")(
        assertTrue(
          Players.selectBot(playerHighLvl.level.level) == Players.BotLvl8
        )
      )
    )
}
