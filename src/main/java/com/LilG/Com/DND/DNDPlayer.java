package com.LilG.Com.DND;

import org.jetbrains.annotations.NotNull;
import org.pircbotx.User;

import java.util.ArrayList;
import java.util.List;

import static com.LilG.Com.utils.LilGUtil.randInt;
import static com.LilG.Com.utils.LilGUtil.searchEnum;

/**
 * Created by ggonz on 10/28/2015.
 * The Class the holds and handles all of the Players info
 */


public class DNDPlayer {
    final static int XPLevelDivider = 24;
    private final List<Items> inventory = new ArrayList<>();
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private int[] stats = new int[DNDStats.values().length];
    private User player;
    private int maxHP = 100;
    private int HP = maxHP;
    private int XP = 0;
    private int level = 1;
    private String name;
    private String race;
    private DNDClasses Class;
    private DNDFamiliar familiar;
    private int XPToGain = 0;

    public DNDPlayer(String name, String race, String Class, User user) {
        this.name = name;
        this.player = user;
        this.race = race;
        this.Class = getClassFromString(Class);

    }

    public DNDPlayer(String name, String race, String Class, User user, String familiarName, String familiar) {
        this(name, race, Class, user);
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

    private DNDClasses getClassFromString(String stat) {
        return searchEnum(DNDClasses.class, stat);
    }

    public String getName() {
        return name;
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

    public boolean addInventory(String item) {
        return addInventory(searchEnum(Items.class, item));
    }

    public boolean removeFromInventory(String item) {
        return removeFromInventory(searchEnum(Items.class, item));
    }

    public boolean addInventory(Items item) {
        return inventory.add(item);
    }

    public boolean removeFromInventory(Items item) {
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

    public int getStat(DNDStats stat) {
        return stats[stat.ordinal()];
    }

    public String toString() {
        try {
            return (name + ". HP: " + getHPAmounts() + ". Level: " + level + ". XP: " + getXPAmounts() + ". Race: " + race + ". Class: " + Class + " Current items: " + getInventory() + ". Familiar: " + familiar.getName() + ". HP: " + familiar.getHPAmounts());
        } catch (Exception e) {
            return "Error: " + e;
        }
    }

    public enum DNDStats {
        Attack,
        Defense,
        Intelligence,
        Speed,
        Mana
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
