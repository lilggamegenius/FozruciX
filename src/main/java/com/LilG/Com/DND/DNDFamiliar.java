package com.LilG.Com.DND;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.LilG.Com.utils.LilGUtil.randInt;

/**
 * Created by ggonz on 10/30/2015.
 */
public class DNDFamiliar {
    private final static int XPLevelDivider = DNDPlayer.XPLevelDivider;
    private final List<DNDPlayer.Items> inventory = new ArrayList<>();
    //private final DNDPlayer owner;
    private final DNDPlayer.DNDFamiliars species;
    private int[] stats = new int[DNDPlayer.DNDStats.values().length];
    private int maxHP = 100;
    private int HP = maxHP;
    private int XP = 0;
    private int level = 1;
    private String name;

    DNDFamiliar(String name, /*DNDPlayer owner,*/ DNDPlayer.DNDFamiliars species) {
        this.name = name;
        //this.owner = owner;
        this.species = species;
    }

    public String getName() {
        return name;
    }

    private String getClassName(DNDPlayer.@NotNull DNDClasses stat) {
        return stat.name();
    }

    public void addHP(int HP) {
        this.HP = this.HP + HP;
        if (this.HP > maxHP) {
            this.HP = maxHP;
        }
    }

    public void hit(int hitAmount) {
        HP = HP - hitAmount;
    }

    public void addXP(int XP) {
        this.XP = XP + this.XP;
        checkIfLevelUp();
    }

    private void checkIfLevelUp() {
        if (XP >= XPLevelDivider * level) {
            XP -= (XPLevelDivider * level);
            level++;
            maxHP += randInt(10, 25);
            HP = maxHP;
        }
    }

    public String getInventory() {
        String invenStr = "<Empty>";
        if (!inventory.isEmpty()) {
            invenStr = inventory.toString();
        }
        return invenStr;
    }

    public boolean addInventory(DNDPlayer.Items item) {
        return inventory.add(item);
    }

    public boolean removeFromInventory(DNDPlayer.Items item) {
        return inventory.remove(item);
    }

    @NotNull
    public String getHPAmounts() {
        return HP + "/" + maxHP;
    }

    @NotNull
    public String getXPAmounts() {
        return XP + "/" + XPLevelDivider * level;
    }

    public int getStat(DNDPlayer.DNDStats stat) {
        return stats[stat.ordinal()];
    }

    @NotNull
    public String toString() {
        return "Name: " + name + /*". Owner: " + owner.getName() +*/ " HP: " + HP + "/" + maxHP + ". Level: " + level + ". XP: " + XP + "/" + XPLevelDivider * level + ". Species: " + species.name();
    }


}
