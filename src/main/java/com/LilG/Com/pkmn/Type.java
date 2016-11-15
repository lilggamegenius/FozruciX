package com.LilG.Com.pkmn;

/**
 * Created by lil-g on 11/14/16.
 */
enum Type {//    Strong                  Weak                   Other
    /*0*/   Bug(new byte[]{1, 9, 14}, new byte[]{6, 7, 15}, new byte[]{}),
    /*1*/   Dark(new byte[]{}, new byte[]{}, new byte[]{}),
    /*2*/   Dragon(new byte[]{}, new byte[]{}, new byte[]{}),
    /*3*/   Electric(new byte[]{}, new byte[]{}, new byte[]{}),
    /*4*/   Fairy(new byte[]{}, new byte[]{}, new byte[]{}),
    /*5*/   Fighting(new byte[]{}, new byte[]{}, new byte[]{}),
    /*6*/   Fire(new byte[]{}, new byte[]{}, new byte[]{}),
    /*7*/   Flying(new byte[]{}, new byte[]{}, new byte[]{}),
    /*8*/   Ghost(new byte[]{}, new byte[]{}, new byte[]{}),
    /*9*/   Grass(new byte[]{}, new byte[]{}, new byte[]{}),
    /*10*/  Ground(new byte[]{}, new byte[]{}, new byte[]{}),
    /*11*/  Ice(new byte[]{}, new byte[]{}, new byte[]{}),
    /*12*/  Normal(new byte[]{}, new byte[]{}, new byte[]{}),
    /*13*/  Poison(new byte[]{}, new byte[]{}, new byte[]{}),
    /*14*/  Psychic(new byte[]{}, new byte[]{}, new byte[]{}),
    /*15*/  Rock(new byte[]{}, new byte[]{}, new byte[]{}),
    /*16*/  Steel(new byte[]{}, new byte[]{}, new byte[]{}),
    /*17*/  Water(new byte[]{}, new byte[]{}, new byte[]{});

    Type[] strong, weak, other;


    Type(byte[] strong, byte[] weak, byte other[]) {
        this.strong = new Type[strong.length];
        this.weak = new Type[weak.length];
        this.other = new Type[other.length];

        for (byte i = 0; i < strong.length; i++) {
            this.strong[i] = Type.values()[strong[i]];
        }
        for (byte i = 0; i < weak.length; i++) {
            this.weak[i] = Type.values()[weak[i]];
        }
        for (byte i = 0; i < other.length; i++) {
            this.other[i] = Type.values()[other[i]];
        }
    }
}