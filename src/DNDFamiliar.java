import java.util.List;

/**
 * Created by ggonz on 10/30/2015.
 */
public class DNDFamiliar extends DNDPlayer {
    final int XPLevelDivider = 24;
    int maxHP = 75;
    int HP = maxHP;
    String name;
    String originalOwner;
    String owner;
    DNDFamiliars species;
    int XPToGain = 0;
    int XP = 0;
    int level = 1;
    int[] stats = {10, 10, 10, 10, 10, 10};

    List<String> inventory;
    List<String> actions;


    public DNDFamiliar(String name, String owner, DNDFamiliars species) {
        this.name = owner;
        this.owner = owner;
        this.originalOwner = owner;
        this.species = species;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "Name: " + name + ". Owner: " + owner + " HP: " + HP + "/" + maxHP + ". Level: " + level + ". XP: " + XP + "/" + XPLevelDivider * level + ". Species: " + species.name();
    }


}
