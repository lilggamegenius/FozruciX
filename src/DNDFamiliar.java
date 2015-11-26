/**
 * Created by ggonz on 10/30/2015.
 */
public class DNDFamiliar extends DNDPlayer {
    String name;
    String originalOwner;
    String owner;
    DNDFamiliars species;

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
