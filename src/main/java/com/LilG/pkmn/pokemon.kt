package com.LilG.pkmn

import java.util.*

/**
 * Created by lil-g on 11/14/16.
 */
interface pokemon {

    val health: Short

    val maxHealth: Short

    val name: String

    val mainType: Type

    val secondaryType: Type

    val status: BitSet

    fun setStatus(status: StatusEffect): Boolean

    fun getStatus(status: StatusEffect): Boolean

    fun doDamage(damageToDeal: Int, type: Type): Int

    fun takeDamage(damageTaken: Int, type: Type): Int

    val ability: Ability
}