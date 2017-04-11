package com.LilG.m68k

import com.sun.jna.Library
import com.sun.jna.NativeLong

/**
 * Created by ggonz on 8/16/2016.
 * Interface for the M68k simulator
 */
interface M68kSim : Library {

    fun start()

    fun exit()

    fun setByte(address: Short, num: Byte)

    fun setWord(address: Short, num: Short)

    fun setLongWord(address: Short, num: NativeLong)

    fun addByte(address: Short, num: Byte)

    fun addWord(address: Short, num: Short)

    fun addLongWord(address: Short, num: NativeLong)

    fun getByte(address: Short): Byte

    fun getWord(address: Short): Short

    fun getLongWord(address: Short): NativeLong

    fun clearMem()

    val ramStart: Long

    val ramSize: Short

    fun lea(address: Short, An: Int)

    fun pea(address: Short)

    fun add(size: Int, dn: Int, ea: Short)

    fun add(size: Int, ea: Short, dn: Int)

    fun adda(size: Size, ea: Short, an: Int)

    fun addi(size: Int, ea: Short, data: NativeLong)

    fun and(size: Int, dn: Int, ea: Short)

    fun and(size: Int, ea: Short, dn: Int)

    fun move(size: Int, source: Short, destination: Short)

    fun move(size: Int, source: Short, dn: Int)

    fun move(size: Int, dn: Int, destination: Short)

    fun move(size: Int, dn1: Int, dn2: Int)

    fun moveq(data: Byte, destination: Short)

    fun memDump()

    enum class Size private constructor(val size: kotlin.Byte, val symbol: Char) {
        Byte(8.toByte(), 'b'), Word(16.toByte(), 'w'), LongWord(32.toByte(), 'l')
    }

    enum class DataRegister {
        d0, d1, d2, d3, d4, d5, d6, d7
    }

    enum class AddressRegister {
        a0, a1, a2, a3, a4, a5, a6, a7
    }
}
