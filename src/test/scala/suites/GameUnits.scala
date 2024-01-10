package suites

import zio.test._
import zio.test.Assertion._

import java.util.UUID

import units.Characters

object GameUnitTests {
  val uuid = UUID.fromString("b2c8ccb8-191a-4233-9b34-3e3111a4adaf")

  val playerSecondLvl =
    Characters.CustomCharacter(Characters.CharacterData(uuid, "Melody", 55))

  val playerHighLvl =
    Characters.CustomCharacter(
      Characters.CharacterData(uuid, "Pumpkin Jack", 1500)
    )

  val playerCreation =
    test("Should create a player with expected fields")(
      assert(playerSecondLvl)(
        hasField(
          "name",
          (p: Characters.CustomCharacter) => p.name,
          equalTo("Melody")
        ) &&
          hasField(
            "level",
            (p: Characters.CustomCharacter) => p.level.level,
            equalTo(2)
          ) &&
          hasField(
            "attack",
            (p: Characters.CustomCharacter) => p.level.attack,
            equalTo(12)
          ) &&
          hasField(
            "hitPoints",
            (p: Characters.CustomCharacter) => p.level.hitPoints,
            equalTo(13)
          ) &&
          hasField(
            "reward",
            (p: Characters.CustomCharacter) => p.level.reward,
            equalTo(11)
          )
      )
    )

  val botSelection =
    suite("Should select the bot of correct level")(
      test("Should select BotLvl2")(
        assertTrue(
          Characters.selectBot(
            playerSecondLvl.level.level
          ) == Characters.BotLvl2
        )
      ),
      test("Should select BotLvl8")(
        assertTrue(
          Characters.selectBot(playerHighLvl.level.level) == Characters.BotLvl8
        )
      )
    )
}
