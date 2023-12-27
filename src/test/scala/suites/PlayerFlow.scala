package suites

import zio.http._
import zio.ZIO
import zio.json._
import zio.test._
import zio.test.Assertion._

import app.PlayerApp
import app.PlayerFlow
import state.PlayersRepo
import units.Players

object PlayerFlowTest {

  def mkPlayerData(exp: Int) =
    Players.PlayerData(GameFlowTest.playerUuid, "Melody", exp)

  val createPlayerSuccess =
    test("Should successfully create a new character, update and delete it") {

      for {
        _ <- TestRandom.feedUUIDs(GameFlowTest.playerUuid)
        createPlayerResponse <- PlayerFlow.createPlayer("Melody")
        playerDataFromStorage <- PlayerFlow.getPlayer(GameFlowTest.playerId)
        updatePlayerData <- PlayersRepo.updatePlayer(
          GameFlowTest.playerUuid,
          50
        )
        updatedDataFromStorage <- PlayerFlow.getPlayer(GameFlowTest.playerId)
        deleteResponse <- PlayerFlow.deletePlayer(GameFlowTest.playerId)
        deletedDataFromStorage <- PlayerFlow.getPlayer(GameFlowTest.playerId)
      } yield assertTrue(
        createPlayerResponse == Response.json(
          GameFlowTest.mkPlayerData(0).toJson
        ),
        playerDataFromStorage == Response.json(
          GameFlowTest.mkPlayerData(0).toJson
        ),
        updatePlayerData == Option(mkPlayerData(50)),
        updatedDataFromStorage == Response.json(
          GameFlowTest.mkPlayerData(50).toJson
        ),
        deleteResponse == Response.json(
          GameFlowTest.mkPlayerData(50).toJson
        ),
        deletedDataFromStorage == Response.status(Status.NotFound)
      )
    }
  val updatePlayerFailTest =
    test("Should not update anything if player is not in storage") {
      for {
        updatePlayerData <- PlayersRepo.updatePlayer(
          GameFlowTest.playerUuid,
          50
        )
        updatedDataFromStorage <- PlayerFlow.getPlayer(GameFlowTest.playerId)
      } yield assertTrue(
        updatePlayerData == None,
        updatedDataFromStorage == Response.status(Status.NotFound)
      )
    }
  val deletePlayerFailTest =
    test("Should not delete anything if player is not in storage") {
      for {
        deletedPlayerData <- PlayersRepo.deletePlayer(GameFlowTest.playerUuid)
        deletedDataFromStorage <- PlayerFlow.getPlayer(GameFlowTest.playerId)
      } yield assertTrue(
        deletedPlayerData == None,
        deletedDataFromStorage == Response.status(Status.NotFound)
      )
    }
}
