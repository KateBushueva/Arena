package userdata

import zio.json._

object GameUserData {

  final case class StartGameData(characterId: String)
  final case class HitData(battleId: String, characterId: String)
  final case class CompleteBattleData(battleId: String)

  object StartGameData {
    implicit val startGameDataEncoder: JsonEncoder[StartGameData] =
      DeriveJsonEncoder.gen[StartGameData]
    implicit val startGameDataDecoder: JsonDecoder[StartGameData] =
      DeriveJsonDecoder.gen[StartGameData]
  }

  object HitData {
    implicit val hitDataEncoder: JsonEncoder[HitData] =
      DeriveJsonEncoder.gen[HitData]
    implicit val hitDataDecoder: JsonDecoder[HitData] =
      DeriveJsonDecoder.gen[HitData]
  }

  object CompleteBattleData {
    implicit val completeBattleDataEncoder: JsonEncoder[CompleteBattleData] =
      DeriveJsonEncoder.gen[CompleteBattleData]
    implicit val completeBattleDataDecoder: JsonDecoder[CompleteBattleData] =
      DeriveJsonDecoder.gen[CompleteBattleData]
  }
}
