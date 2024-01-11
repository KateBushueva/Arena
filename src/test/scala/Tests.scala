import zio._
import zio.test._
import zio.Scope
import suites._
import state.instances.InMemoryGameState
import java.util.UUID
import units.Game
import instances.InMemoryPlayerStorage
import units.Characters

object TestSuites extends ZIOSpecDefault {
  def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Test cases")(
      suite("GameFlow success tests")(
        GameFlowTest.gameFlowSuccess
      ).provideShared(Layers.battleStorage ++ Layers.playerStorage),
      suite("GameFlow fail tests")(
        GameFlowTest.hitTestFail,
        GameFlowTest.deleteTestFail
      ).provideShared(Layers.battleStorage ++ Layers.playerStorage),
      suite("PlayerFlow success tests")(
        PlayerFlowTest.createPlayerSuccess
      ).provideShared(Layers.playerStorage),
      suite("PlayerFlow fail tests")(
        PlayerFlowTest.updatePlayerFailTest,
        PlayerFlowTest.deletePlayerFailTest
      ).provideShared(Layers.playerStorage),
      suite("Unit Tests")(
        GameUnitTests.playerCreation,
        GameUnitTests.botSelection
      )
    )
}

object Layers {
  val battleStorage =
    ZLayer.scoped(
      ZIO.acquireRelease(
        Ref
          .make(Map.empty[UUID, Game.BattleState])
          .map(new InMemoryGameState(_)) <* ZIO.debug(
          "New InMemory battle storage initialized"
        )
      )(storage => storage.getBattles.debug("InMemory battle storage"))
    )
  val playerStorage =
    ZLayer.scoped(
      ZIO.acquireRelease(
        Ref
          .make(Map.empty[UUID, Characters.CharacterData])
          .map(new InMemoryPlayerStorage(_)) <* ZIO.debug(
          "New InMemory player storage initialized"
        )
      )(storage => storage.getAllCharacters.debug("InMemory player storage"))
    )
}
