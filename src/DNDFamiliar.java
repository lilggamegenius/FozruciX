/**
 * Created by ggonz on 10/30/2015.
 */
public class DNDFamiliar extends DNDPlayer {
    private final String name;
    private final String owner;
    private final DNDFamiliars species;

    public DNDFamiliar(String name, String owner, DNDFamiliars species) {
        this.name = name;
        this.owner = owner;
        this.species = species;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "Name: " + name + ". Owner: " + owner + " HP: " + HP + "/" + maxHP + ". Level: " + level + ". XP: " + XP + "/" + XPLevelDivider * level + ". Species: " + species.name();
    }


}
