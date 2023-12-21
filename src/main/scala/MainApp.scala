import zio._
import zio.http._

import app._
import state.InMemoryGameState
import state.PersistentPlayersRepoLayer
import io.getquill.jdbczio.Quill
import io.getquill.SnakeCase

object MainApp extends ZIOAppDefault {
  def run: ZIO[Environment with ZIOAppArgs with Scope, Throwable, Any] = {
    val httpApps = GameApp() ++ PlayerApp()

    Server
      .serve(httpApps.withDefaultErrorResponse)
      .provide(
        Server.defaultWithPort(8080),
        InMemoryGameState.layer,
        Quill.DataSource.fromPrefix("playerDatabaseConfig"),
        PersistentPlayersRepoLayer.layer
      )
  }
}
