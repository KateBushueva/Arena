package suites

import zio.http._
import zio.ZIO
import zio.json._
import zio.test._
import zio.test.Assertion._

import app.GameFlow._
import state.GameState
import java.util.UUID
import units._
import units.Game.BattleState._
import app.CharacterFlow
import units.Characters

object GameFlowTest {
  val battleId = "b2c8ccb8-191a-4233-9b34-3e3111a4adaf"
  val playerId = "b2c81118-191a-4233-9b34-3e3111a4adaf"
  val gameUuid = UUID.fromString(battleId)
  val playerUuid = UUID.fromString(playerId)

  val player =
    Characters.CustomCharacter(
      Characters.CharacterData(playerUuid, "Melody", 0)
    )

  def mkBattleStateResponse(damage1: Int, damage2: Int): Response = {
    val response = Game.BattleState(
      gameUuid,
      player,
      Characters.BotLvl1,
      damage1,
      damage2
    )
    Response.json(response.toJson)
  }

  def mkCharacterData(exp: Int): Characters.CharacterData =
    Characters.CharacterData(playerUuid, "Melody", exp)

  val gameFlowSuccess =
    test("Should successfully run start, hit and delete endpoints") {
      for {
        _ <- TestRandom.feedUUIDs(playerUuid, gameUuid)
        _ <- TestRandom.feedInts(4)
        createPlayerResponse <- CharacterFlow.createCharacter("Melody")
        startResponse <- startWithBot(playerId)
        battleState1 <- getBattle(battleId)
        hitResponse1 <- hit(battleId)
        _ <- hit(battleId)
        hitResponse2 <- hit(battleId)
        battleState2 <- getBattle(battleId)
        completeResponse <- completeBattle(battleId)
        allBattles <- getAllBattles()
        updatedPlayer <- CharacterFlow.getCharacter(playerId)

      } yield assertTrue(
        createPlayerResponse == Response.json(mkCharacterData(0).toJson),
        startResponse == Response.text(battleId),
        battleState1 == mkBattleStateResponse(0, 0),
        hitResponse1 == mkBattleStateResponse(3, 4),
        hitResponse2 == Response.text("The battle is over, Melody won"),
        battleState2 == mkBattleStateResponse(6, 8),
        completeResponse == Response.text(
          s"The battle is over, Melody won. 8 experience received"
        ),
        allBattles == Response.json(
          Map.empty[UUID, Game.BattleState].map(_.toJson).toJson
        ),
        updatedPlayer == Response.json(
          mkCharacterData(8).toJson
        )
      )
    }

  val hitTestFail = test("Hit test. Fail case - not found response") {
    for {
      response <- hit(battleId)
    } yield assertTrue(
      response == Response.status(Status.NotFound)
    )
  }

  val deleteTestFail = test("Delete test. Fail case - not found response") {
    for {
      response <- completeBattle(battleId)
    } yield assertTrue(
      response == Response.status(Status.NotFound)
    )
  }
}
