package com.LilG.Com.DND;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ggonz on 10/28/2015.
 * The Class the holds and handles all of the Players info
 */


public class DNDPlayer{

    final int XPLevelDivider = 24;
	private final int maxMoney = 0;
	//
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private final int[] stats = {10, 10, 10, 10, 10, 10};
    //ToDo
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private final boolean[] passives = {false, false, false, false, false, false};
    private final List<String> inventory = new ArrayList<>();
	int maxHP = 100;
    int HP = maxHP;
    int XP = 0;
    int level = 1;
	private String playerName;
	private String race;
	private DNDClasses Class;
	private DNDFamiliar familiar;
	private int XPToGain = 0;

	DNDPlayer(){

    }

    public DNDPlayer(String playerName, String race, String Class, String usersName) {
        this.playerName = playerName;
	    this.race = race;
	    this.Class = getClassFromString(Class);
        //this.passives = passives;
    }

    public DNDPlayer(String playerName, String race, String Class, String usersName, String familiarName, String familiar) {
        this.playerName = playerName;
	    this.race = race;
	    this.Class = getClassFromString(Class);
        this.familiar = new DNDFamiliar(familiarName, playerName, DNDFamiliars.valueOf(familiar));
    }

    public static boolean ifClassExists(String str) {
        for (DNDClasses me : DNDClasses.values()) {
            if (me.name().equalsIgnoreCase(str))
                return true;
        }
        return false;
    }

    public static boolean ifSpeciesExists(String str) {
        for (DNDFamiliars me : DNDFamiliars.values()) {
            if (me.name().equalsIgnoreCase(str))
                return true;
        }
        return false;
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(@SuppressWarnings("SameParameterValue") int min, @SuppressWarnings("SameParameterValue") int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

        return rand.nextInt((max - min) + 1) + min;
    }

    private DNDClasses getClassFromString(String stat) {
        return DNDClasses.valueOf(stat);
    }

    public String getPlayerName() {
        return playerName;
    }

    private String getClassName(@NotNull DNDClasses stat) {
        return stat.name();
	}

	public void addHP(int HP){
		this.HP = this.HP + HP;
        if (this.HP > maxHP) {
            this.HP = maxHP;
        }
    }

    public void hit(int hitAmount) {
        HP = HP - hitAmount;
    }

    public DNDFamiliar getFamiliar() {
        return familiar;
    }

	public void addXP(int XP){
		this.XP = XP + this.XP;
        checkIfLevelUp();
    }

	private void checkIfLevelUp(){
		if (XP >= XPLevelDivider * level) {
            XP -= (XPLevelDivider * level);
            level++;
            maxHP += randInt(10, 25);
            HP = maxHP;
        }
    }

	public String getInventory(){
		String invenStr = "<Empty>";
        if (!inventory.isEmpty()) {
            invenStr = inventory.toString();
        }
        return invenStr;
    }

	public void addInventory(String item){
		inventory.add(item);
    }

    public void removeFromInventory(String item) {
        inventory.remove(inventory.indexOf(item));
    }

    public String toString() {
        String invenStr = "<Empty>";
        if (!inventory.isEmpty()) {
            invenStr = inventory.toString();
        }
        try {
            return (playerName + ". HP: " + getHPAmounts() + ". Level: " + level + ". XP: " + getXPAmounts() + ". Race: " + race + ". Class: " + Class + " Current items: " + invenStr + ". Familiar: " + familiar.getName() + ". HP: " + familiar.getHPAmounts());
        } catch (Exception e) {
            return "Error: " + e;
        }
    }

    @NotNull
    public String getHPAmounts() {
        return HP + "/" + maxHP;
	}

    @NotNull
    public String getXPAmounts() {
        return XP + "/" + XPLevelDivider * level;
	}

	public enum DNDStats{
		Dexterity
	}

    public enum DNDClasses {
        Barbarian, Bard, Cleric, Druid, Wizard, Mage, Monk_Mystic, Paladin, Ranger, Sorcerer, Thief_Rogue, Warlock
    }

    public enum DNDFamiliars {
	    Bat, Cat, Hawk, Lizard, Owl, Rat, Raven, Toad, Weasel, Ferret, Hedgehog, Mouse, Thrush, Leopard, Wolverine, Albatross, Parrot, SeaSnake, ArcticFox, Fox, Dog, Monkey, Platypus, Rabbit, Squirrel, Badger, Chipmunk, Eagle, Groundhog, Otter, Pokemon
    }

}
