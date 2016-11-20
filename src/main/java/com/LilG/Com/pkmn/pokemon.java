package com.LilG.Com.pkmn;

import java.util.BitSet;

/**
 * Created by lil-g on 11/14/16.
 */
public interface pokemon {

    short getHealth();

    short getMaxHealth();

    String getName();

    Type getMainType();

    Type getSecondaryType();

    BitSet getStatus();

    boolean setStatus(StatusEffect status);

    boolean getStatus(StatusEffect status);

    int doDamage(int damageToDeal, Type type);

    int takeDamage(int damageTaken, Type type);

    Ability getAbility();
}