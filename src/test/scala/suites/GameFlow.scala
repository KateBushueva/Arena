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

object GameFlowTest {
  val id = "b2c8ccb8-191a-4233-9b34-3e3111a4adaf"
  val uuid = UUID.fromString(id)

  val player = Players.CustomPlayer(
    UUID.fromString("b2c8ccb8-191a-4233-9b34-3e3111a4bbbb"),
    "Melody"
  )

  def mkBattleStateResponse(damage1: Int, damage2: Int): Response = {
    val response = Game.BattleState(
      uuid,
      player,
      Players.BotLvl1,
      damage1,
      damage2
    )
    Response.json(response.toJson)
  }

  val gameFlowSuccess =
    test("Should successfully run start, hit and delete endpoints") {
      for {
        _ <- TestRandom.feedUUIDs(uuid)
        _ <- TestRandom.feedInts(4)
        startResponse <- startNewBattle(player)
        battleState1 <- getBattle(id)
        hitResponse1 <- hit(id)
        _ <- hit(id)
        hitResponse2 <- hit(id)
        battleState2 <- getBattle(id)
        deleteResponse <- deleteBattle(id)
        allBattles <- getAllBattles()

      } yield assertTrue(
        startResponse == Response.text(id),
        battleState1 == mkBattleStateResponse(0, 0),
        hitResponse1 == mkBattleStateResponse(3, 4),
        hitResponse2 == Response.text("The battle is over, Melody won"),
        battleState2 == mkBattleStateResponse(6, 8),
        deleteResponse == Response.text(s"$id battle deleted successfully"),
        allBattles == Response.json(
          Map.empty[UUID, Game.BattleState].map(_.toJson).toJson
        )
      )
    }

  val hitTestFail = test("Hit test. Fail case - not found response") {
    for {
      response <- hit(id)
    } yield assertTrue(
      response == Response.status(Status.NotFound)
    )
  }

  val deleteTestFail = test("Delete test. Fail case - not found response") {
    for {
      response <- deleteBattle(id)
    } yield assertTrue(
      response == Response.status(Status.NotFound)
    )
  }

}
