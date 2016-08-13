package com.LilG.Com.DND;

import org.jetbrains.annotations.NotNull;

/**
 * Created by ggonz on 10/30/2015.
 */
public class DNDFamiliar extends DNDPlayer {
    private final String name;
    private final DNDPlayer owner;
    private final DNDFamiliars species;

    DNDFamiliar(String name, DNDPlayer owner, DNDFamiliars species) {
        super();
        this.name = name;
        this.owner = owner;
        this.species = species;
    }

    public String getName() {
        return name;
    }

    @NotNull
    public String toString() {
        return "Name: " + name + ". Owner: " + owner + " HP: " + HP + "/" + maxHP + ". Level: " + level + ". XP: " + XP + "/" + XPLevelDivider * level + ". Species: " + species.name();
    }


}
