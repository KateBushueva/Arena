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
import units.Game.FightState._

object GameFlowTest {
  val id = "b2c8ccb8-191a-4233-9b34-3e3111a4adaf"
  val uuid = UUID.fromString(id)

  def mkFightStateResponse(damage1: Int, damage2: Int): Response = {
    val response = Game.FightState(
      uuid,
      Players.DefaultPlayer(1, "Melody"),
      Players.Bot(1),
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
        startResponse <- startNewFight("Melody")
        fightState1 <- getFight(id)
        hitResponse1 <- hit(id)
        _ <- hit(id)
        hitResponse2 <- hit(id)
        fightState2 <- getFight(id)
        deleteResponse <- deleteFight(id)
        allFights <- getAllFights()

      } yield assertTrue(
        startResponse == Response.text(id),
        fightState1 == mkFightStateResponse(0, 0),
        hitResponse1 == mkFightStateResponse(3, 4),
        hitResponse2 == Response.text("The fight is over, Melody won"),
        fightState2 == mkFightStateResponse(6, 8),
        deleteResponse == Response.text(s"$id fight deleted successfully"),
        allFights == Response.json(
          Map.empty[UUID, Game.FightState].map(_.toJson).toJson
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
      response <- deleteFight(id)
    } yield assertTrue(
      response == Response.status(Status.NotFound)
    )
  }

}
