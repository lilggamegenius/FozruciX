package com.LilG.Com.DND;

import org.jetbrains.annotations.NotNull;
import org.pircbotx.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ggonz on 10/28/2015.
 * The Class the holds and handles all of the Players info
 */


public class DNDPlayer {

    final int XPLevelDivider = 24;
    private final int maxMoney = 0;
    //
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private final int[] stats = {10, 10, 10, 10, 10};
    //ToDo
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private final boolean[] passives = {false, false, false, false, false, false};
    private final List<String> inventory = new ArrayList<>();
    User player;
    int maxHP = 100;
    int HP = maxHP;
    int XP = 0;
    int level = 1;
    private String playerName;
    private String race;
    private DNDClasses Class;
    private DNDFamiliar familiar;
    private int XPToGain = 0;

    DNDPlayer() {
    }

    public DNDPlayer(String playerName, String race, String Class, User user) {
        this.playerName = playerName;
        this.player = user;
        this.race = race;
        this.Class = getClassFromString(Class);
        //this.passives = passives;
    }

    public DNDPlayer(String playerName, String race, String Class, User user, String familiarName, String familiar) {
        this.playerName = playerName;
        this.player = user;
        this.race = race;
        this.Class = getClassFromString(Class);
        this.familiar = new DNDFamiliar(familiarName, this, DNDFamiliars.valueOf(familiar));
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

    public User getPlayer() {
        return player;
    }

    private String getClassName(@NotNull DNDClasses stat) {
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

    public DNDFamiliar getFamiliar() {
        return familiar;
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

    public void addInventory(String item) {
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

    public enum DNDStats {
        Attack(0),
        Defense(1),
        Intelligence(2),
        Speed(3),
        Mana(4);

        private int numVal;

        DNDStats(int numVal) {
            this.numVal = numVal;
        }

        public int val() {
            return numVal;
        }
    }

    public enum DNDClasses {
        Barbarian(10, 7, 4, 5, 2, PreferredWeaponType.HeavyMelee),
        Cleric(7, 5, 7, 6, 7, PreferredWeaponType.Spellbook),
        Druid(8, 6, 5, 6, 4, PreferredWeaponType.Melee),
        Wizard(8, 5, 10, 5, 10, PreferredWeaponType.Spellbook),
        Mage(6, 6, 9, 5, 9, PreferredWeaponType.Spellbook),
        Monk_Mystic(6, 5, 5, 5, 6, PreferredWeaponType.Spellbook),
        Paladin(8, 5, 9, 5, 9, PreferredWeaponType.Spellbook),
        Ranger(9, 7, 4, 7, 4, PreferredWeaponType.Melee),
        Sorcerer(7, 6, 10, 4, 10, PreferredWeaponType.Spellbook),
        Thief_Rogue(6, 6, 7, 10, 4, PreferredWeaponType.Projectile),
        Warlock(10, 6, 3, 7, 1, PreferredWeaponType.Spellbook);

        final int baseAttack;
        final int baseDefense;
        final int baseIntelligence;
        final int baseSpeed;
        final int baseMana;
        final PreferredWeaponType weaponType;

        DNDClasses(int baseAttack, int baseDefense, int baseIntelligence, int baseSpeed, int baseMana, PreferredWeaponType weaponType) {
            this.baseAttack = baseAttack;
            this.baseDefense = baseDefense;
            this.baseIntelligence = baseIntelligence;
            this.baseSpeed = baseSpeed;
            this.baseMana = baseMana;
            this.weaponType = weaponType;
        }

        public int[] getBaseStats() {
            return new int[]{baseAttack, baseDefense, baseIntelligence, baseSpeed, baseMana};
        }

        public int getBaseAttack() {
            return baseAttack;
        }

        public int getBaseDefense() {
            return baseDefense;
        }

        public int getBaseIntelligence() {
            return baseIntelligence;
        }

        public int getBaseSpeed() {
            return baseSpeed;
        }

        public int getBaseMana() {
            return baseMana;
        }

        public PreferredWeaponType getWeaponType() {
            return weaponType;
        }
    }

    public enum DNDFamiliars {
        Bat, Cat, Hawk, Lizard, Owl, Rat, Raven, Toad, Weasel, Ferret, Hedgehog, Mouse, Thrush, Leopard, Wolverine, Albatross, Parrot, SeaSnake, ArcticFox, Fox, Dog, Monkey, Platypus, Rabbit, Squirrel, Badger, Chipmunk, Eagle, Groundhog, Otter, Pokemon
    }

    public enum PreferredWeaponType {
        Melee, HeavyMelee, Projectile, Spellbook
    }

    public enum Items {
        Potion(0.25f, ItemType.Healing),
        GreatPotion(0.5f, ItemType.Healing),
        GreaterPotion(0.75f, ItemType.Healing),
        FullPotion(1f, ItemType.Healing),
        ManaPotion(0.3f, ItemType.Mana),
        GreatManaPotion(0.6f, ItemType.Mana),
        GreaterManaPotion(0.8f, ItemType.Mana),
        FullManaPotion(1f, ItemType.Mana),
        Revive(0.5f, ItemType.Revive),
        FullRevive(1f, ItemType.Revive);

        final ItemType itemType;
        final float multiplier;

        Items(float multiplier, ItemType itemType) {
            this.multiplier = multiplier;
            this.itemType = itemType;
        }

        public ItemType getItemType() {
            return itemType;
        }

        public float getMultiplier() {
            return multiplier;
        }
    }

    public enum ItemType {
        Healing, Mana, Revive
    }

}
