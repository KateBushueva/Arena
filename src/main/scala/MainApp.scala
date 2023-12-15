import zio._
import zio.http._

import app._
import state.InMemoryGameState

object MainApp extends ZIOAppDefault {
  def run = {
    val httpApp = GameApp()

    Server
      .serve(httpApp.withDefaultErrorResponse)
      .provide(
        Server.defaultWithPort(8080),
        InMemoryGameState.layer
      )
  }
}
