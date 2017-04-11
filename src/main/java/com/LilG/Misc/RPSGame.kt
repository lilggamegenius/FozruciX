package com.LilG.Misc

/**
 * Created by ggonz on 12/13/2015.
 */
class RPSGame(private val player1: String, private val player2: String) {

    private var choice1 = 0
    private var choice2 = 0

    fun isInGame(playerToCheck: String): Boolean {
        return playerToCheck.equals(player1, ignoreCase = true) || playerToCheck.equals(player2, ignoreCase = true)
    }

    fun isFirstPlayer(playerToCheck: String): Boolean {
        return playerToCheck.equals(player1, ignoreCase = true)
    }

    fun setP1Choice(choice: Int) {
        this.choice1 = choice
    }

    fun setP2Choice(choice: Int) {
        this.choice2 = choice
    }

    val isGameSet: Boolean
        get() = choice1 != 0 && choice2 != 0

}
