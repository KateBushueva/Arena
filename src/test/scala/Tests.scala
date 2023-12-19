import zio._
import zio.test._
import zio.Scope
import suites._
import state.InMemoryGameState
import java.util.UUID
import units.Game

object TestSuites extends ZIOSpecDefault {
  def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Game tests")(
      GameFlowTest.gameFlowSuccess,
      GameFlowTest.hitTestFail,
      GameFlowTest.deleteTestFail
    ).provideShared(Layers.inMemoryState)
}

object Layers {
  val inMemoryState =
    ZLayer.scoped(
      ZIO.acquireRelease(
        Ref
          .make(Map.empty[UUID, Game.FightState])
          .map(new InMemoryGameState(_)) <* ZIO.debug(
          "New InMemoryState initialized"
        )
      )(storage => storage.getFights.debug("InMemory storage"))
    )
}
