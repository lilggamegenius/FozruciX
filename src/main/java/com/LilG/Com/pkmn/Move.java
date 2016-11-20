package com.LilG.Com.pkmn;

/**
 * Created by lil-g on 11/14/16.
 */
public enum Move {
    Absorb(Type.Grass, MoveCategory.Special, "A nutrient-draining attack. The user's HP is restored by half the damage taken by the target."),
    Acid(Type.Poison, MoveCategory.Special, "The opposing Pokémon are attacked with a spray of harsh acid. This may also lower their Sp. Def stats."),
    AcidArmor(Type.Poison, MoveCategory.Other, "The user alters its cellular structure to liquefy itself, sharply raising its Defense stat."),
    AcidSpray(Type.Poison, MoveCategory.Special, "The user spits fluid that works to melt the target. This harshly lowers the target's Sp. Def stat."),
    Acrobatics(Type.Flying, MoveCategory.Physical, "The user nimbly strikes the target. If the user is not holding an item, this attack inflicts massive damage."),
    Acupressure(Type.Normal, MoveCategory.Other, "The user applies pressure to stress points, sharply boosting one of its stats or its allies' stats."),
    AerialAce(Type.Flying, MoveCategory.Physical, "The user confounds the target with speed, then slashes. This attack never misses."),
    Aeroblast(Type.Flying, MoveCategory.Special, "A vortex of air is shot at the target to inflict damage. Critical hits land more easily."),
    AfterYou(Type.Normal, MoveCategory.Other, "The user helps the target and makes it use its move right after the user."),
    Agility(Type.Psychic, MoveCategory.Other, "The user relaxes and lightens its body to move faster. This sharply raises the Speed stat."),
    AirCutter(Type.Flying, MoveCategory.Special, "The user launches razor-like wind to slash the opposing Pokémon. Critical hits land more easily."),
    AirSlash(Type.Flying, MoveCategory.Special, "The user attacks with a blade of air that slices even the sky. This may also make the target flinch."),
    AllySwitch(Type.Psychic, MoveCategory.Other, "The user teleports using a strange power and switches places with one of its allies."),
    Amnesia(Type.Psychic, MoveCategory.Other, "The user temporarily empties its mind to forget its concerns. This sharply raises the user's Sp. Def stat."),
    AncientPower(Type.Rock, MoveCategory.Special, "The user attacks with a prehistoric power. This may also raise all the user's stats at once."),
    AquaJet(Type.Water, MoveCategory.Physical, "The user lunges at the target at a speed that makes it almost invisible. This move always goes first."),
    AquaRing(Type.Water, MoveCategory.Other, "The user envelops itself in a veil made of water. It regains some HP every turn."),
    AquaTail(Type.Water, MoveCategory.Physical, "The user attacks by swinging its tail as if it were a vicious wave in a raging storm."),
    ArmThrust(Type.Fighting, MoveCategory.Physical, "The user looses a flurry of open-palmed arm thrusts that hit two to five times in a row."),
    Aromatherapy(Type.Grass, MoveCategory.Other, "The user releases a soothing scent that heals all status conditions affecting the user's party."),
    Assist(Type.Normal, MoveCategory.Other, "The user hurriedly and randomly uses a move among those known by immune Pokémon in the party."),
    Assurance(Type.Dark, MoveCategory.Physical, "If the target has already taken some damage in the same turn, this attack's power is doubled."),
    Astonish(Type.Ghost, MoveCategory.Physical, "The user attacks the target while shouting in a startling fashion. This may also make the target flinch."),
    AttackOrder(Type.Bug, MoveCategory.Physical, "The user calls out its underlings to pummel the target. Critical hits land more easily."),
    Attract(Type.Normal, MoveCategory.Other, "If it is the opposite gender of the user, the target becomes infatuated and less likely to attack."),
    AuraSphere(Type.Fighting, MoveCategory.Special, "The user looses a blast of aura power from deep within its body at the target. This attack never misses."),
    AuroraBeam(Type.Ice, MoveCategory.Special, "The target is hit with a rainbow-colored beam. This may also lower the target's Attack stat."),
    Autotomize(Type.Steel, MoveCategory.Other, "The user sheds part of its body to make itself lighter and sharply raise its Speed stat."),
    Avalanche(Type.Ice, MoveCategory.Physical, "An attack move that inflicts double the damage if the user has been hurt by the target in the same turn."),;
    Type type;
    MoveCategory category;
    String description;

    Move(Type type, MoveCategory category, String description) {
        this.type = type;
        this.category = category;
        this.description = description;
    }

    enum MoveCategory {
        Physical, Special, Other
    }
}



