package com.LilG.Com.pkmn;

/**
 * Created by lil-g on 11/14/16.
 */
enum Type {//    Strong                  Weak to                Immune to      Not very effective
    /*0*/ Bug(new byte[]{1, 9, 14}, new byte[]{6, 7, 15}, new byte[]{}, new byte[]{}),
    /*1*/ Dark(new byte[]{8, 14}, new byte[]{0, 4, 5}, new byte[]{14}, new byte[]{}),
    /*2*/ Dragon(new byte[]{2}, new byte[]{2, 4, 11}, new byte[]{}, new byte[]{4}),
    /*3*/ Electric(new byte[]{7, 17}, new byte[]{10}, new byte[]{}, new byte[]{10}),
    /*4*/ Fairy(new byte[]{1, 2, 5}, new byte[]{13, 16}, new byte[]{2}, new byte[]{}),
    /*5*/ Fighting(new byte[]{1, 11, 12, 16}, new byte[]{4, 7, 14}, new byte[]{}, new byte[]{8}),
    /*6*/ Fire(new byte[]{0, 9, 11, 16}, new byte[]{10, 15, 17}, new byte[]{}, new byte[]{}),
    /*7*/ Flying(new byte[]{0, 5, 9}, new byte[]{3, 11, 15}, new byte[]{10}, new byte[]{}),
    /*8*/ Ghost(new byte[]{8, 14}, new byte[]{1, 14}, new byte[]{5, 12}, new byte[]{12}),
    /*9*/ Grass(new byte[]{}, new byte[]{}, new byte[]{}, new byte[]{}),
    /*10*/Ground(new byte[]{}, new byte[]{}, new byte[]{}, new byte[]{}),
    /*11*/Ice(new byte[]{}, new byte[]{}, new byte[]{}, new byte[]{}),
    /*12*/Normal(new byte[]{}, new byte[]{}, new byte[]{}, new byte[]{}),
    /*13*/Poison(new byte[]{}, new byte[]{}, new byte[]{}, new byte[]{}),
    /*14*/Psychic(new byte[]{}, new byte[]{}, new byte[]{}, new byte[]{}),
    /*15*/Rock(new byte[]{}, new byte[]{}, new byte[]{}, new byte[]{}),
    /*16*/Steel(new byte[]{}, new byte[]{}, new byte[]{}, new byte[]{}),
    /*17*/Water(new byte[]{}, new byte[]{}, new byte[]{}, new byte[]{});

    Type[] strong, weak, immune, notEffective;


    Type(byte[] strong, byte[] weak, byte immune[], byte notEffective[]) {
        this.strong = new Type[strong.length];
        this.weak = new Type[weak.length];
        this.immune = new Type[immune.length];
        this.notEffective = new Type[notEffective.length];

        for (byte i = 0; i < strong.length; i++) {
            this.strong[i] = Type.values()[strong[i]];
        }
        for (byte i = 0; i < weak.length; i++) {
            this.weak[i] = Type.values()[weak[i]];
        }
        for (byte i = 0; i < immune.length; i++) {
            this.immune[i] = Type.values()[immune[i]];
        }
        for (byte i = 0; i < notEffective.length; i++) {
            this.notEffective[i] = Type.values()[notEffective[i]];
        }
    }
}