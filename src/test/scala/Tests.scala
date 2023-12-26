import zio._
import zio.test._
import zio.Scope
import suites._
import state.InMemoryGameState
import java.util.UUID
import units.Game
import instances.InMemoryPlayerStorage
import units.Players

object TestSuites extends ZIOSpecDefault {
  def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Game tests")(
      GameFlowTest.gameFlowSuccess,
      GameFlowTest.hitTestFail,
      GameFlowTest.deleteTestFail
    ).provideShared(Layers.battleStorage ++ Layers.playerStorage)
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
          .make(Map.empty[UUID, Players.PlayerData])
          .map(new InMemoryPlayerStorage(_)) <* ZIO.debug(
          "New InMemory player storage initialized"
        )
      )(storage => storage.getAllPlayers.debug("InMemory player storage"))
    )
}
