package suites

import zio.http._
import zio.ZIO
import zio.json._
import zio.test._
import zio.test.Assertion._

import app.CharacterApp
import app.CharacterFlow
import state.PlayersRepo
import units.Characters

object PlayerFlowTest {

  def mkCharacterData(exp: Int) =
    Characters.CharacterData(GameFlowTest.playerUuid, "Melody", exp)

  val createPlayerSuccess =
    test("Should successfully create a new character, update and delete it") {

      for {
        _ <- TestRandom.feedUUIDs(GameFlowTest.playerUuid)
        createPlayerResponse <- CharacterFlow.createPlayer("Melody")
        characterDataFromStorage <- CharacterFlow.getPlayer(
          GameFlowTest.playerId
        )
        updateCharacterData <- PlayersRepo.updatePlayer(
          GameFlowTest.playerUuid,
          50
        )
        updatedDataFromStorage <- CharacterFlow.getPlayer(GameFlowTest.playerId)
        deleteResponse <- CharacterFlow.deletePlayer(GameFlowTest.playerId)
        deletedDataFromStorage <- CharacterFlow.getPlayer(GameFlowTest.playerId)
      } yield assertTrue(
        createPlayerResponse == Response.json(
          GameFlowTest.mkCharacterData(0).toJson
        ),
        characterDataFromStorage == Response.json(
          GameFlowTest.mkCharacterData(0).toJson
        ),
        updateCharacterData == Option(mkCharacterData(50)),
        updatedDataFromStorage == Response.json(
          GameFlowTest.mkCharacterData(50).toJson
        ),
        deleteResponse == Response.json(
          GameFlowTest.mkCharacterData(50).toJson
        ),
        deletedDataFromStorage == Response.status(Status.NotFound)
      )
    }
  val updatePlayerFailTest =
    test("Should not update anything if player is not in storage") {
      for {
        updateCharacterData <- PlayersRepo.updatePlayer(
          GameFlowTest.playerUuid,
          50
        )
        updatedDataFromStorage <- CharacterFlow.getPlayer(GameFlowTest.playerId)
      } yield assertTrue(
        updateCharacterData == None,
        updatedDataFromStorage == Response.status(Status.NotFound)
      )
    }
  val deletePlayerFailTest =
    test("Should not delete anything if player is not in storage") {
      for {
        deletedCharacterData <- PlayersRepo.deletePlayer(
          GameFlowTest.playerUuid
        )
        deletedDataFromStorage <- CharacterFlow.getPlayer(GameFlowTest.playerId)
      } yield assertTrue(
        deletedCharacterData == None,
        deletedDataFromStorage == Response.status(Status.NotFound)
      )
    }
}
