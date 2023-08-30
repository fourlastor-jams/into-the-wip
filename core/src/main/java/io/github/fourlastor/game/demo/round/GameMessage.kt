package io.github.fourlastor.game.demo.round

enum class GameMessage {
  ROUND_START,
  TURN_START,
  TURN_END,
  ABILITY_START,
  NEXT_STEP,
  ABILITY_END;

  fun handles(code: Int): Boolean {
    return ordinal == code
  }
}
