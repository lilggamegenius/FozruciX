package com.LilG.pkmn

/**
 * Created by lil-g on 11/14/16.
 */
enum class Type private constructor(strong: ByteArray, weak: ByteArray, immune: ByteArray, notEffective: ByteArray) {//    Strong                  Weak to                Immune to      Not very effective
/*0*/ Bug(byteArrayOf(1, 9, 14), byteArrayOf(6, 7, 15), byteArrayOf(), byteArrayOf()),
    /*1*/ Dark(byteArrayOf(8, 14), byteArrayOf(0, 4, 5), byteArrayOf(14), byteArrayOf()),
    /*2*/ Dragon(byteArrayOf(2), byteArrayOf(2, 4, 11), byteArrayOf(), byteArrayOf(4)),
    /*3*/ Electric(byteArrayOf(7, 17), byteArrayOf(10), byteArrayOf(), byteArrayOf(10)),
    /*4*/ Fairy(byteArrayOf(1, 2, 5), byteArrayOf(13, 16), byteArrayOf(2), byteArrayOf()),
    /*5*/ Fighting(byteArrayOf(1, 11, 12, 16), byteArrayOf(4, 7, 14), byteArrayOf(), byteArrayOf(8)),
    /*6*/ Fire(byteArrayOf(0, 9, 11, 16), byteArrayOf(10, 15, 17), byteArrayOf(), byteArrayOf()),
    /*7*/ Flying(byteArrayOf(0, 5, 9), byteArrayOf(3, 11, 15), byteArrayOf(10), byteArrayOf()),
    /*8*/ Ghost(byteArrayOf(8, 14), byteArrayOf(1, 14), byteArrayOf(5, 12), byteArrayOf(12)),
    /*9*/ Grass(byteArrayOf(), byteArrayOf(), byteArrayOf(), byteArrayOf()),
    /*10*/Ground(byteArrayOf(), byteArrayOf(), byteArrayOf(), byteArrayOf()),
    /*11*/Ice(byteArrayOf(), byteArrayOf(), byteArrayOf(), byteArrayOf()),
    /*12*/Normal(byteArrayOf(), byteArrayOf(), byteArrayOf(), byteArrayOf()),
    /*13*/Poison(byteArrayOf(), byteArrayOf(), byteArrayOf(), byteArrayOf()),
    /*14*/Psychic(byteArrayOf(), byteArrayOf(), byteArrayOf(), byteArrayOf()),
    /*15*/Rock(byteArrayOf(), byteArrayOf(), byteArrayOf(), byteArrayOf()),
    /*16*/Steel(byteArrayOf(), byteArrayOf(), byteArrayOf(), byteArrayOf()),
    /*17*/Water(byteArrayOf(), byteArrayOf(), byteArrayOf(), byteArrayOf());

    var strong: Array<Type?> = arrayOfNulls(strong.size)
    var weak: Array<Type?> = arrayOfNulls(weak.size)
    var immune: Array<Type?> = arrayOfNulls(immune.size)
    var notEffective: Array<Type?> = arrayOfNulls(notEffective.size)


    init {
        for (i in strong.indices) {
            this.strong[i] = Type.values()[strong[i].toInt()]
        }
        for (i in weak.indices) {
            this.weak[i] = Type.values()[weak[i].toInt()]
        }
        for (i in immune.indices) {
            this.immune[i] = Type.values()[immune[i].toInt()]
        }
        for (i in notEffective.indices) {
            this.notEffective[i] = Type.values()[notEffective[i].toInt()]
        }
    }
}