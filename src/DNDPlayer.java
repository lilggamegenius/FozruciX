import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ggonz on 10/28/2015.
 */


public class DNDPlayer implements Serializable {

    final int XPLevelDivider = 24;
    String playerName;
    String usersName;
    boolean Male = true;
    String race;
    DNDClasses Class;
    DNDFamiliar familiar;
    int maxHP = 100;
    int HP = maxHP;
    int maxMoney = 0;
    int money = maxMoney;
    int XPToGain = 0;
    int XP = 0;
    int level = 1;
    //
    int[] stats = {10, 10, 10, 10, 10, 10};
    //ToDo
    boolean[] passives = {false, false, false, false, false, false};
    List<String> inventory = new ArrayList<>();
    List<String> actions = new ArrayList<>();

    public DNDPlayer() {

    }

    public DNDPlayer(String playerName, String race, String Class, String usersName) {
        this.playerName = playerName;
        this.usersName = usersName;
        this.race = race;
        this.Class = getClassFromString(Class);
        //this.passives = passives;
    }

    public DNDPlayer(String playerName, String race, String Class, String usersName, String familiarName, String familiar) {
        this.playerName = playerName;
        this.usersName = usersName;
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

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public void setClass(String aClass) {
        Class = getClassFromString(aClass);
    }

    public String getclass() {
        return getClassName(Class);
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getHP() {
        return HP;
    }

    public void addHP(int HP) {
        this.HP = this.HP + HP;
        if (this.HP > maxHP) {
            this.HP = maxHP;
        }
    }

    public String getHPAmounts() {
        return HP + "/" + maxHP;
    }

    public void hit(int hitAmount) {
        HP = HP - hitAmount;
    }

    public DNDFamiliar getFamiliar() {
        return familiar;
    }

    public void setFamiliar(DNDFamiliar familiar) {
        this.familiar = familiar;
    }

    public int getXPToGain() {
        return XPToGain;
    }

    public void addXPToGain(int XPToGain) {
        this.XPToGain = this.XPToGain + XPToGain;
    }

    public void storeXPToGain() {
        XP = XP + XPToGain;
        XPToGain = 0;
    }

    public int getXP() {
        return XP;
    }

    public void addXP(int XP) {
        this.XP = XP + this.XP;
        checkIfLevelUp();
    }

    public void checkIfLevelUp() {
        if (XP >= XPLevelDivider * level) {
            XP -= (XPLevelDivider * level);
            level++;
            maxHP += MyBotX.randInt(10, 25);
            HP = maxHP;
        }
    }

    public String getXPAmounts() {
        return XP + "/" + XPLevelDivider * level;
    }

    public int getLevel() {
        return level;
    }

    public int[] getStats() {
        return stats;
    }

    public int getStat(int statToGet) {
        return stats[statToGet];
    }

    public int getAttack() {
        int roll = MyBotX.randInt(-5, 15);
        if (roll < 1) {
            double ans = roll + stats[DNDStats.Dexterity.ordinal()] + roll * (0.25 * level);
            return (int) Math.round(ans);
        } else {
            return roll;
        }
    }

    public void addStats(int statToAdd, int stats) {
        this.stats[statToAdd] = stats + this.stats[statToAdd];
    }

    public void subStats(int statToSub, int stats) {
        this.stats[statToSub] = this.stats[statToSub] - stats;
    }

    public boolean[] getPassives() {
        return passives;
    }

    public void setPassives(int passiveToSet, boolean whatToSet) {
        this.passives[passiveToSet] = whatToSet;
    }

    public String getInventory() {
        String invenStr = "<Empty>";
        if (!inventory.isEmpty()) {
            invenStr = inventory.toString();
        }
        return invenStr;
    }

    public boolean isInInventory(String item) {
        if (inventory.contains(item)) {
            return true;
        }
        return false;
    }

    public int findInIventory(String item) {
        return inventory.indexOf(item);
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

    public String getStatName(DNDStats stat) {
        return stat.name();
    }

    public int getStatIndex(DNDStats stat) {
        return stat.ordinal();
    }

    public DNDStats getStatFromString(String stat) {
        return DNDStats.valueOf(stat);
    }

    public String getClassName(DNDClasses stat) {
        return stat.name();
    }

    public int getClassIndex(DNDStats stat) {
        return stat.ordinal();
    }

    public DNDClasses getClassFromString(String stat) {
        return DNDClasses.valueOf(stat);
    }

    public enum DNDStats {
        Strength, Dexterity, Constitution, Intelligence, Wisdom, Charisma;
    }

    public enum DNDClasses {
        Barbarian, Bard, Cleric, Druid, Wizard_Mage, Monk_Mystic, Paladin, Ranger, Sorcerer, Thief_Rogue, Warlock;
    }

    public enum DNDFamiliars {
        Bat, Cat, Hawk, Lizard, Owl, Rat, Raven, Toad, Weasel, Ferret, Hedgehog, Mouse, Thrush, Leopard, Wolverine, Albatross, Parrot, SeaSnake, ArcticFox, Fox, Dog, Monkey, Platypus, Rabbit, Squirrel, Badger, Chipmunk, Eagle, Groundhog, Otter, Pokemon;
    }

}
